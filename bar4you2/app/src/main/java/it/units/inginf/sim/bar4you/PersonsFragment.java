package it.units.inginf.sim.bar4you;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Person> personsList;
    private SharedViewModel sharedViewModel;

    public PersonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonsFragment newInstance(String param1, String param2) {
        PersonsFragment fragment = new PersonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persons, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        personsList = new ArrayList<>();
        if(sharedViewModel.getPersonsMutableLiveData().size() > 0) {
            for (MutableLiveData<Person> personMutableLiveData : sharedViewModel.getPersonsMutableLiveData()) {
                personsList.add(personMutableLiveData.getValue());
            }
            updateInsertedPersonTextView(view);
        }
        EditText personsEditText = view.findViewById(R.id.edit_text_person);
        Button addPersonsButton = new Button(getContext());
        addPersonsButton.setText(R.string.button_insert);
        if(getContext() != null) {
            addPersonsButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        }
        addPersonsButton.setOnClickListener(v -> onClickAddPersonsButton(personsEditText));
        LinearLayout layout = view.findViewById(R.id.persons_fragment);
        layout.addView(addPersonsButton);
        return view;
    }

    private void onClickAddPersonsButton(EditText personsEditText) {
        String[] names = personsEditText.getText().toString().split(",");
        DatabaseReference personsRef = FirebaseDatabase.getInstance()
                .getReference("tables")
                .child("table" + sharedViewModel.getTableNumberMutableLiveData().getValue())
                .child("persons");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].trim(); //Elimino eventuali spazi compresi in names[i]
            DatabaseReference personRef = personsRef.child("person" + (i + 1));
            Person person = new Person(i + 1, names[i]);
            int supportNumber = 1;
            while (!person.nameIsUnique(personsList)) { //Non voglio avere 2 nomi uguali
                names[i] = names[i] + supportNumber;
                person = new Person(i + 1, names[i]);
                supportNumber++;
            }
            personsList.add(person);
            sharedViewModel.addPersonMutableLiveData(person);
            personRef.setValue(person).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.add_person_success,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.add_person_failure,
                            Toast.LENGTH_SHORT).show();
                    Log.d("PersonsFragmentError", "Errore nell'aggiunta di una persona");
                }
            });
        }
        personsEditText.setText("");
        updateInsertedPersonTextView(requireView());
    }

    private void updateInsertedPersonTextView(View view) {
        TextView personsListTextView = view.findViewById(R.id.inserted_person);
        StringBuilder builder = new StringBuilder();
        builder.append("Persone inserite: ");
        for (Person person : personsList) {
            builder.append(person.getName());
            if (personsList.indexOf(person) < personsList.size() - 1) {
                builder.append(", ");
            } else {
                builder.append(".");
            }
        }
        String text = builder.toString();
        personsListTextView.setText(text);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}