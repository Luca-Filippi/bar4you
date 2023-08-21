package it.units.inginf.sim.bar4you;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import android.widget.LinearLayout;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.widget.Space;
import android.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedViewModel sharedViewModel;
    private double totalBill;
    private Button createBillButton;
    private Button createSeparateBillsButton;

    private ExecutorService onClickCreateBillButtonExecutor;
    private ExecutorService onClickCreateSeparateBillsButtonExecutor;
    private ExecutorService fullBillSummaryExecutor;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String param1, String param2) {
        BillFragment fragment = new BillFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Definizione del layout del frammento
        View view;
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (sharedViewModel.getNumberOfOrdersMutableLiveData().getValue() != null &&
                sharedViewModel.getNumberOfOrdersMutableLiveData().getValue() > 0) {
            view = inflater.inflate(R.layout.fragment_bill, container, false);
            createBillSummary(view);
            setBillsButtons(view);
        } else {
            view = inflater.inflate(R.layout.fragment_error, container, false);
            Toast.makeText(requireContext(), R.string.no_order, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void setBillsButtons(View view) {
        Space space = new Space(getContext());
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.space_between_buttons);
        LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                spaceHeight
        );
        LinearLayout layout = view.findViewById(R.id.fragment_bill_id);
        //Aggiungiamo uno spazio tra la tabella e il pulsante
        layout.addView(space, spaceLayoutParams);
        //Bottone per creare un conto unico
        createBillButton = new Button(getContext());
        createBillButton.setText(R.string.bill_button);
        if(getContext() != null) {
            createBillButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        }
        createBillButton.setOnClickListener(v -> onClickCreateBillButton());
        layout.addView(createBillButton);

        // Aggiungiamo uno spazio tra i pulsanti
        Space buttonsSpace = new Space(getContext());
        int buttonsSpaceHeight = getResources().getDimensionPixelSize(R.dimen.space_between_buttons);
        LinearLayout.LayoutParams buttonsSpaceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                buttonsSpaceHeight
        );
        layout.addView(buttonsSpace,buttonsSpaceLayoutParams);

        //Bottone per creare conti separati
        createSeparateBillsButton = new Button(getContext());
        createSeparateBillsButton.setText(R.string.bills_button);
        createSeparateBillsButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        createSeparateBillsButton.setOnClickListener(v -> onClickCreateSeparateBillsButton());
        layout.addView(createSeparateBillsButton);

    }

    private void onClickCreateBillButton() {
        onClickCreateBillButtonExecutor = Executors.newSingleThreadExecutor();
        onClickCreateBillButtonExecutor.submit(() -> {
            DatabaseReference billsRef = FirebaseDatabase.getInstance()
                    .getReference("bills");

            billsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int billNumber = (int) dataSnapshot.getChildrenCount() + 1;
                    DatabaseReference billRef = billsRef.child("bill"+billNumber);
                    if(sharedViewModel.getTableNumberMutableLiveData().getValue() != null) {
                        Bill bill = new Bill(sharedViewModel.getTableNumberMutableLiveData().getValue(), totalBill);
                        billRef.setValue(bill)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), R.string.create_bill_success, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                                        sharedViewModel.resetSharedViewModel();
                                        startActivity(new Intent(requireContext(), ErrorActivity.class));
                                        onClickCreateBillButtonExecutor.shutdown();
                                    }
                                });
                        DatabaseReference tableRef = FirebaseDatabase.getInstance()
                                .getReference("tables")
                                .child("table" + sharedViewModel.getTableNumberMutableLiveData().getValue());
                        tableRef.child("free").setValue(true); //Libero il tavolo
                        tableRef.child("persons").removeValue(); //Elimino le persone dal tavolo
                        tableRef.child("orders").removeValue(); //Elimino gli ordini effettuati

                        // Esegue le operazioni di UI sulla UI thread
                        requireActivity().runOnUiThread(() -> {
                            createBillButton.setClickable(false);
                            createSeparateBillsButton.setClickable(false);
                            //Faccio partire l'attività end
                            startActivity(new Intent(requireContext(), EndActivity.class));
                        });
                        // Chiude l'executor dopo che tutte le operazioni sono state completate
                        onClickCreateBillButtonExecutor.shutdown();
                    } else {
                        Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                        Log.d("BillFragmentError","Errore nella creazione del conto");
                        sharedViewModel.resetSharedViewModel();
                        startActivity(new Intent(requireContext(), ErrorActivity.class));
                        onClickCreateBillButtonExecutor.shutdown();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                    Log.d("BillFragmentError","Errore nella creazione del conto");
                    sharedViewModel.resetSharedViewModel();
                    startActivity(new Intent(requireContext(), ErrorActivity.class));
                    onClickCreateBillButtonExecutor.shutdown();
                }
            });
        });
    }

    private void onClickCreateSeparateBillsButton() {
        onClickCreateSeparateBillsButtonExecutor = Executors.newSingleThreadExecutor();
        onClickCreateSeparateBillsButtonExecutor.submit(() -> {
            DatabaseReference billsRef = FirebaseDatabase.getInstance()
                    .getReference("bills");

            billsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int billNumber = (int) dataSnapshot.getChildrenCount() + 1;
                    for(MutableLiveData<Person> personMutableLiveData: sharedViewModel.getPersonsMutableLiveData()) {
                        DatabaseReference billRef = billsRef.child("bill" + billNumber);
                        if (sharedViewModel.getTableNumberMutableLiveData().getValue() != null &&
                                personMutableLiveData.getValue() != null) {
                            Bill bill = new Bill(sharedViewModel.getTableNumberMutableLiveData().getValue(),
                                    personMutableLiveData.getValue().getBill());
                            billRef.setValue(bill)
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(requireContext(), R.string.create_bills_success +
                                                    personMutableLiveData.getValue().getName(), Toast.LENGTH_SHORT).show();
                                            sharedViewModel.resetSharedViewModel();
                                            startActivity(new Intent(requireContext(), ErrorActivity.class));
                                            onClickCreateSeparateBillsButtonExecutor.shutdown();
                                        }
                                    });
                            billNumber++;
                            Toast.makeText(requireContext(), R.string.create_bills_failure, Toast.LENGTH_SHORT).show();

                        } else {
                            sharedViewModel.resetSharedViewModel();
                            Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                            Log.d("BillFragmentError","Errore nella creazione del conto");
                            startActivity(new Intent(requireContext(), ErrorActivity.class));
                            onClickCreateSeparateBillsButtonExecutor.shutdown();
                            break;
                        }
                    }
                    DatabaseReference tableRef = FirebaseDatabase.getInstance()
                            .getReference("tables")
                            .child("table" + sharedViewModel.getTableNumberMutableLiveData().getValue());
                    tableRef.child("free").setValue(true); //Libero il tavolo
                    tableRef.child("persons").removeValue(); //Elimino le persone dal tavolo
                    tableRef.child("orders").removeValue(); //Elimino gli ordini effettuati
                    sharedViewModel.resetSharedViewModel();
                    // Chiude l'executor dopo che tutte le operazioni sono state completate
                    onClickCreateSeparateBillsButtonExecutor.shutdown();
                    // Esegue le operazioni di UI sulla UI thread
                    requireActivity().runOnUiThread(() -> {
                        createBillButton.setClickable(false);
                        createSeparateBillsButton.setClickable(false);
                        //Faccio partire l'attività end
                        startActivity(new Intent(requireContext(), EndActivity.class));
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                    Log.d("BillFragmentError","Errore nella creazione del conto");
                    sharedViewModel.resetSharedViewModel();
                    startActivity(new Intent(requireContext(), ErrorActivity.class));
                    onClickCreateSeparateBillsButtonExecutor.shutdown();
                }
            });
        });
    }

    private void createBillSummary(View view) {
        TableLayout tableLayout = view.findViewById(R.id.bill_table);
        tableLayout.removeAllViews();
        TableRow row = new TableRow(requireContext());

        TextView nameTextView = new TextView(requireContext());
        nameTextView.setText(R.string.name);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        nameTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(nameTextView);

        TextView spentTextView = new TextView(requireContext());
        spentTextView.setText(R.string.total_spent);
        spentTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        spentTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(spentTextView);

        tableLayout.addView(row);
        row = new TableRow(requireContext());
        tableLayout.addView(row);
        fullBillSummary();
    }

    private void fullBillSummary() {
        fullBillSummaryExecutor = Executors.newSingleThreadExecutor();
        fullBillSummaryExecutor.submit(() -> {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                    .getReference("tables")
                    .child("table" + sharedViewModel.getTableNumberMutableLiveData().getValue())
                    .child("orders");

            for(MutableLiveData<Person> personMutableLiveData: sharedViewModel.getPersonsMutableLiveData()) {
                String personName = personMutableLiveData.getValue().getName();
                for(int i = 1; i <= sharedViewModel.getNumberOfOrdersMutableLiveData().getValue(); i++) {
                    DatabaseReference orderRef = ordersRef.child("order"+i);
                    orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot orderElementSnapshot : dataSnapshot.getChildren()) {
                                DataSnapshot personSnapshot = orderElementSnapshot.child("personName");
                                if (personSnapshot.exists()) {
                                    String name = personSnapshot.child("name").getValue(String.class);
                                    if (name != null && name.equals(personName)) {
                                        double increment = orderElementSnapshot.child("drink")
                                                .child("price").getValue(Double.class);
                                        personMutableLiveData.getValue().incrementBill(increment);
                                    }
                                } else {
                                    sharedViewModel.resetSharedViewModel();
                                    Toast.makeText(requireContext(), R.string.create_bill_failure, Toast.LENGTH_SHORT).show();
                                    Log.d("BillFragmentError","Errore nel riempimento della tabella");
                                    startActivity(new Intent(requireContext(), ErrorActivity.class));
                                    onClickCreateSeparateBillsButtonExecutor.shutdown();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(requireContext(), R.string.database_error,
                                    Toast.LENGTH_SHORT).show();
                            sharedViewModel.resetSharedViewModel();
                            startActivity(new Intent(requireContext(), ErrorActivity.class));
                        }
                    });
                }
            }
            // Esegue le operazioni di UI sulla UI thread
            requireActivity().runOnUiThread(this::createBillSummaryLayout);
            fullBillSummaryExecutor.shutdown();
        });
    }

    private void createBillSummaryLayout() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        TableLayout tableLayout = requireView().findViewById(R.id.bill_table);
        totalBill = 0.00;
        for(MutableLiveData<Person> personMutableLiveData: sharedViewModel.getPersonsMutableLiveData()) {
            TableRow row = new TableRow(requireContext());
            String name;
            if(personMutableLiveData.getValue() != null) {
                name = personMutableLiveData.getValue().getName();
            } else {
                Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show();
                sharedViewModel.resetSharedViewModel();
                startActivity(new Intent(requireContext(), ErrorActivity.class));
                break;
            }
            TextView nameTextView = new TextView(requireContext());
            nameTextView.setText(name);
            nameTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            nameTextView.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(nameTextView);
            TextView billTextView = new TextView(requireContext());
            billTextView.setText(decimalFormat.format(personMutableLiveData.getValue().getBill()));
            billTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f));
            billTextView.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(billTextView);
            tableLayout.addView(row);
            totalBill += personMutableLiveData.getValue().getBill();
        }
        TableRow row = new TableRow(requireContext());
        TextView totalTextView = new TextView(requireContext());
        totalTextView.setText(R.string.total);
        totalTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        totalTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(totalTextView);
        TextView totalBillTextView = new TextView(requireContext());
        totalBillTextView.setText(decimalFormat.format(totalBill));
        totalBillTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                1f));
        totalBillTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(totalBillTextView);
        tableLayout.addView(row);
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
    public void onDestroy() {
        super.onDestroy();
        //controllo che gli executor siano chiusi
        if (onClickCreateBillButtonExecutor != null && !onClickCreateBillButtonExecutor.isShutdown()) {
            onClickCreateBillButtonExecutor.shutdownNow();
        }

        if (onClickCreateSeparateBillsButtonExecutor != null && !onClickCreateSeparateBillsButtonExecutor.isShutdown()) {
            onClickCreateSeparateBillsButtonExecutor.shutdownNow();
        }

        if (fullBillSummaryExecutor != null && !fullBillSummaryExecutor.isShutdown()) {
            fullBillSummaryExecutor.shutdownNow();
        }
    }
}