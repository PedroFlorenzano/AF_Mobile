package com.codingstuff.SeriesTracking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingstuff.SeriesTracking.Adapter.ToDoAdapter;
import com.codingstuff.SeriesTracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask  extends BottomSheetDialogFragment {

    private ToDoAdapter adapter;
    public static final String TAG = "AddNewTask";

    private TextView setDueDate;
    private EditText mTaskEdit;
    private Button mSaveBtn;

    private Button mUpdateEpBtn;

    private EditText mPlataforma;

    private EditText mTemporada;

    private EditText mEpisodioAtual;
    private FirebaseFirestore firestore;
    private Context context;
    private String dueDate = "";
    private String id = "";
    private String dueDateUpdate = "";

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task , container , false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_tv);
        mTaskEdit = view.findViewById(R.id.task_edittext);
        mSaveBtn = view.findViewById(R.id.save_btn);
        mUpdateEpBtn = view.findViewById(R.id.incrementEp);
        mPlataforma = view.findViewById(R.id.edtPlataforma);
        mTemporada = view.findViewById(R.id.edtTemporada);
        mEpisodioAtual = view.findViewById(R.id.edtUltimoEp);

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            String temporada = bundle.getString("temporada");
            String plataforma = bundle.getString("plataforma");
            String episodioAtual = bundle.getString("episodioAtual");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("due");

            mTaskEdit.setText(task);
            mPlataforma.setText(plataforma);
            mTemporada.setText(temporada);
            mEpisodioAtual.setText(episodioAtual);
            setDueDate.setText(dueDateUpdate);
            
        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (s.toString().equals("")){
                   mSaveBtn.setEnabled(false);
                   mSaveBtn.setBackgroundColor(Color.GRAY);
               }else{
                   mSaveBtn.setEnabled(true);
                   mSaveBtn.setBackgroundColor(getResources().getColor(R.color.green_blue));
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        setDueDate.setText(dayOfMonth + "/" + month + "/" + year);
                        dueDate = dayOfMonth + "/" + month +"/"+year;

                    }
                } , YEAR , MONTH , DAY);

                datePickerDialog.show();
            }
        });

        boolean finalIsUpdate = isUpdate;
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String task = mTaskEdit.getText().toString();
                String plataforma = mPlataforma.getText().toString();
                String temporada = mTemporada.getText().toString();
                String episodioAtual = mEpisodioAtual.getText().toString();

                Map<String, Object> updateSerie = new HashMap<>();
                updateSerie.put("task", task);
                updateSerie.put("due", dueDate);
                updateSerie.put("plataforma", plataforma);
                updateSerie.put("temporada", temporada);
                updateSerie.put("episodioAtual", episodioAtual);
                updateSerie.put("status", 0);
                updateSerie.put("time", FieldValue.serverTimestamp());

                if (finalIsUpdate){
                    firestore.collection("task").document(id).update(updateSerie);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();

                }
                else {
                    if (task.isEmpty()) {
                        Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show();
                    } else {

                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("plataforma", plataforma);
                        taskMap.put("temporada", temporada);
                        taskMap.put("episodioAtual", episodioAtual);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());

                        firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });

        mUpdateEpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!id.isEmpty()) {
                    DocumentReference taskRef = firestore.collection("task").document(id);
                    taskRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String episodioAtualFounded = document.getString("episodioAtual");

                                Integer episodioAtualNumber = Integer.valueOf(episodioAtualFounded);
                                episodioAtualNumber = episodioAtualNumber + 1;

                                String episodioAtual = String.valueOf(episodioAtualNumber);

                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("episodioAtual", episodioAtual);

                                taskRef.update(updateData)
                                        .addOnSuccessListener(aVoid -> {
                                            mEpisodioAtual.setText(episodioAtual);
                                            Toast.makeText(context, "Episódio incrementado com sucesso", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Erro ao incrementar episódio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(context, "Documento não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Erro ao obter dados: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof  OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }
}
