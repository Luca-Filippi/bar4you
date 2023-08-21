package it.units.inginf.sim.bar4you;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExecutorService onClickSendButtonExecutor;

    private SharedViewModel sharedViewModel;

    public SendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFragment newInstance(String param1, String param2) {
        SendFragment fragment = new SendFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Definizione del layout del frammento
        View view;
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if(sharedViewModel.getOrderMutableLiveData().size() > 0) {
            view = inflater.inflate(R.layout.fragment_send, container, false);
            TableLayout tableLayout = view.findViewById(R.id.order_table);
            createOrderSummary(tableLayout);
            setSendButton(view);
        } else {
            view = inflater.inflate(R.layout.fragment_error, container, false);
            Toast.makeText(requireContext(), R.string.no_order_element, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void createOrderSummary(TableLayout tableLayout) {
        tableLayout.removeAllViews();
        TableRow row = new TableRow(requireContext());

        TextView productTextView = new TextView(requireContext());
        productTextView.setText(R.string.product);
        productTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        productTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(productTextView);

        TextView priceTextView = new TextView(requireContext());
        priceTextView.setText(R.string.price);
        priceTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        priceTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(priceTextView);

        TextView personTextView = new TextView(requireContext());
        personTextView.setText(R.string.persons_spinner);
        personTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        personTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(personTextView);

        TextView addTextView = new TextView(requireContext());
        addTextView.setText(R.string.remove);
        addTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        addTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        row.addView(addTextView);

        tableLayout.addView(row);
        row = new TableRow(requireContext());
        tableLayout.addView(row);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        for(MutableLiveData<OrderElement> orderElementMutableLiveData: sharedViewModel.getOrderMutableLiveData()) {
            //Definizione degli attributi interessanti dell'ordine
            String product = orderElementMutableLiveData.getValue().getDrink().getName();
            double price = orderElementMutableLiveData.getValue().getDrink().getPrice();
            String person = orderElementMutableLiveData.getValue().getPersonName();

            //Definizione del bottone per rimuovere un elemento dall'ordine
            FrameLayout deleteButtonLayout = new FrameLayout(requireContext());
            deleteButtonLayout.setId(View.generateViewId());
            ImageView deleteButtonImage = new ImageView(requireContext());
            deleteButtonImage.setImageResource(R.drawable.delete_image);
            deleteButtonImage.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
            ));
            deleteButtonLayout.addView(deleteButtonImage);
            deleteButtonLayout.setLayoutParams(new TableRow.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            deleteButtonImage.setOnClickListener(v -> onClickDeleteButton(orderElementMutableLiveData,tableLayout));

            TextView productCell = new TextView(requireContext());
            productCell.setText(product);
            productCell.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f));
            productCell.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(productCell);

            TextView priceCell = new TextView(requireContext());
            priceCell.setText(decimalFormat.format(price));
            priceCell.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f));
            priceCell.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(priceCell);

            TextView personCell = new TextView(requireContext());
            personCell.setText(person);
            personCell.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f));
            personCell.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(personCell);

            row.addView(deleteButtonLayout);

            row = new TableRow(requireContext());

            tableLayout.addView(row);
        }
    }

    private void onClickDeleteButton(MutableLiveData<OrderElement> orderElementMutableLiveData, TableLayout tableLayout) {
        //Rimozione del orderElement tra i dati condivisi
        sharedViewModel.removeOrderElementMutableLiveData(orderElementMutableLiveData);

        //Aggiornamento del layout
        tableLayout.removeAllViews(); // Rimuove tutte le righe esistenti
        createOrderSummary(tableLayout);
    }

    private void setSendButton(View view) {
        Button sendButton = new Button(getContext());
        // Imposta il testo del bottone
        sendButton.setText(R.string.submit);
        // Imposta il colore di sfondo del bottone su blu
        if(getContext() != null) {
            sendButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        }
        sendButton.setOnClickListener(v -> onClickSendButton());
        LinearLayout layout = view.findViewById(R.id.fragment_send_id);
        Space space = new Space(getContext());
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.space_between_buttons);
        LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                spaceHeight
        );
        layout.addView(space, spaceLayoutParams);
        layout.addView(sendButton);
    }

    private void onClickSendButton() {
        onClickSendButtonExecutor = Executors.newSingleThreadExecutor();
        onClickSendButtonExecutor.submit(() -> {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                    .getReference("tables")
                    .child("table" + sharedViewModel.getTableNumberMutableLiveData().getValue())
                    .child("orders");

            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sharedViewModel.setNumberOfOrdersMutableLiveData((int) dataSnapshot.getChildrenCount());
                    sharedViewModel.increaseNumberOfOrdersMutableLiveData();
                    DatabaseReference orderRef = ordersRef.child("order" +
                            sharedViewModel.getNumberOfOrdersMutableLiveData().getValue());
                    int i = 1;

                    for (MutableLiveData<OrderElement> orderElementMutableLiveData : sharedViewModel.getOrderMutableLiveData()) {
                        DatabaseReference orderElementRef = orderRef.child("orderElement" + i);
                        MutableLiveData<Person> personMutableLiveData = sharedViewModel.getPersonMutableLiveDataByName(
                                orderElementMutableLiveData.getValue().getPersonName());
                        if(personMutableLiveData != null) {
                            personMutableLiveData.getValue().incrementBill(
                                    orderElementMutableLiveData.getValue().getDrink().getPrice());
                            orderElementRef.setValue(orderElementMutableLiveData.getValue())
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(requireContext(), R.string.error +
                                                    orderElementMutableLiveData.getValue().getDrink().getName() +
                                                    R.string.not_inserted, Toast.LENGTH_SHORT).show();
                                            sharedViewModel.resetSharedViewModel();
                                            startActivity(new Intent(requireContext(), ErrorActivity.class));
                                            onClickSendButtonExecutor.shutdown();
                                        }
                                    });
                        }
                        i++;
                    }
                    Toast.makeText(requireContext(), R.string.send_success, Toast.LENGTH_SHORT).show();
                    // Esegue le operazioni di UI sulla UI thread
                    requireActivity().runOnUiThread(() -> {
                        sharedViewModel.resetOrderMutableLiveData();
                        TableLayout tableLayout = requireView().findViewById(R.id.order_table);
                        tableLayout.removeAllViews();
                    });

                    // Chiude l'executor dopo che tutte le operazioni sono state completate
                    onClickSendButtonExecutor.shutdown();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), R.string.error_count_orders, Toast.LENGTH_SHORT).show();
                    sharedViewModel.resetSharedViewModel();
                    startActivity(new Intent(requireContext(), ErrorActivity.class));
                    onClickSendButtonExecutor.shutdown();
                }
            });
        });
        Toast.makeText(requireContext(), R.string.send_success, Toast.LENGTH_SHORT).show();
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
        if (onClickSendButtonExecutor != null && !onClickSendButtonExecutor.isShutdown()) {
            onClickSendButtonExecutor.shutdownNow();
        }
    }

}