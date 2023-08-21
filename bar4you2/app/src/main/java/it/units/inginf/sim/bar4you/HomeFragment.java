package it.units.inginf.sim.bar4you;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.graphics.Color;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Integer> freeTableNumbers;
    private Button homeButton;
    private SharedViewModel sharedViewModel;
    private ExecutorService onClickExecutor;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Spinner spinner = view.findViewById(R.id.spinner);
        fullSpinner(spinner);
        homeButton = new Button(getContext());
        homeButton.setText(R.string.submit);
        if (getContext() != null) {
            homeButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        }
        homeButton.setOnClickListener(v -> onClickHomeButton(spinner));
        LinearLayout layout = view.findViewById(R.id.home_fragment);
        layout.addView(homeButton);
        return view;
    }

    private void fullSpinner(Spinner spinner) {
        DatabaseReference tablesRef = FirebaseDatabase.getInstance().getReference("tables");
        freeTableNumbers = new ArrayList<>();
        tablesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numberOfTables = (int) dataSnapshot.getChildrenCount();
                for (int i = 1; i <= numberOfTables; i++) {
                    Boolean isFree = dataSnapshot.child("table" + i).child("free").getValue(Boolean.class);
                    if (isFree != null && isFree) {
                        freeTableNumbers.add(dataSnapshot.child("table" + i).child("tableId").getValue(Integer.class));
                    }
                }
                String[ ]items = new String[freeTableNumbers.size() + 1];
                items[0] = "--";
                for (int i = 1; i < items.length; i++) {
                    items[i] = "" + freeTableNumbers.get(i - 1);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_singlechoice, items);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), R.string.count_tables_failure, Toast.LENGTH_SHORT).show();
                Log.d("HomeFragmentError", "Errore nel conteggio dei tavoli");
                sharedViewModel.resetSharedViewModel();
                startActivity(new Intent(requireContext(), ErrorActivity.class));
            }
        });
    }

    public void onClickHomeButton(Spinner spinner) {
        int tableNumber = getTableNumber(spinner);
        if (tableNumber > 0 && tableNumber <= 10) {
            homeButton.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
            homeButton.setClickable(false);
            sharedViewModel.setTableNumberMutableLiveData(tableNumber);
            onClickExecutor = Executors.newSingleThreadExecutor();
            onClickExecutor.submit(() -> {
                        DatabaseReference tablesRef = FirebaseDatabase.getInstance().getReference("tables");

                        tablesRef.child("table" + tableNumber).child("free").setValue(false);
                    });
            PersonsFragment personsFragment = new PersonsFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Sostituisce HomeFragment con PersonsFragment utilizzando il FragmentTransaction
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, personsFragment)
                    .commit();
        }
    }

    public int getTableNumber(Spinner spinner) {
        if(!spinner.getSelectedItem().toString().equals("--")) {
           return Integer.parseInt(spinner.getSelectedItem().toString());
        } else {
            Toast.makeText(requireContext(), R.string.table_number_invalid, Toast.LENGTH_SHORT).show();
            return -1;
        }
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
        if (onClickExecutor != null && !onClickExecutor.isShutdown()) {
            onClickExecutor.shutdownNow();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}