package com.example.fruits;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AnswerTextFragment extends Fragment {

    EditText etJawab;
    String jawab;

    public AnswerTextFragment() {
        super(R.layout.fragment_answer_text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer_text, container, false);

        etJawab = v.findViewById(R.id.etJawab);
        jawab = etJawab.getText().toString();

//        if(jawab!=null || jawab.length()!=0){
//            ((GameActivity)getContext()).setAnswer(jawab);
//        }
        return v;
    }

    public String getAnswer(){
        return etJawab.getText().toString();
    }
}