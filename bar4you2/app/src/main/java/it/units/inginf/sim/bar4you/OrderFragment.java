package it.units.inginf.sim.bar4you;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.TouchDelegate;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected ImageView menuIcon;
    protected SharedViewModel sharedViewModel;
    protected MenuItem selectedItem = null;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if(sharedViewModel.getTableNumberMutableLiveData().getValue() == null &&
                sharedViewModel.getTableNumberMutableLiveData().getValue() <= 0) {
            view = inflater.inflate(R.layout.fragment_order_error, container, false);
            Toast.makeText(requireContext(), R.string.insert_table_number, Toast.LENGTH_SHORT).show();
        } else {
            createMenuLayout(view);
        }

        return view;
    }

    private void showPopupMenu(View view) {
        TableLayout tableLayout = requireView().findViewById(R.id.order_table);
        tableLayout.removeAllViews();
        if (!requireActivity().isFinishing() && !requireActivity().isDestroyed()) {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);

            popupMenu.getMenuInflater().inflate(R.menu.bar_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();

                if(selectedItem != null) {
                    removeStyle(menuItem);
                }

                selectedItem = menuItem;
                addStyle(selectedItem);

                if (itemId == R.id.beers_menu) {
                    createMenu("beers","Birra",DrinkType.BEER);
                    return true;
                } else if (itemId == R.id.wines_menu) {
                    createMenu("wines","Vino",DrinkType.WINE);
                    return true;
                } else if (itemId == R.id.cocktails_menu) {
                    createMenu("cocktails","Cocktail", DrinkType.COCKTAIL);
                    return true;
                } else if (itemId == R.id.soft_drinks_menu) {
                    createMenu("soft_drinks","Analcolico", DrinkType.SOFT_DRINK);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }
    }

    private void createMenuLayout(View view) {
        menuIcon = view.findViewById(R.id.menu_icon);

        // Imposta l'azione di clic sull'immagine per aprire il menu
        menuIcon.setOnClickListener(v -> showPopupMenu(view));

        View parent = (View) menuIcon.getParent(); // Otteniamo il genitore dell'ImageView
        parent.post(() -> {
            // Impostazione dell'area di tocco
            Rect touchRect = new Rect();
            menuIcon.getHitRect(touchRect);
            touchRect.top -= 100; // Diminuisce il valore per espandere l'area di tocco verso l'alto
            touchRect.right += 100; // Aumenta il valore per espandere l'area di tocco verso destra
            touchRect.bottom += 100; // Aumenta il valore per espandere l'area di tocco verso il basso
            touchRect.left -= 100; // Diminuisce il valore per espandere l'area di tocco verso sinistra

            TouchDelegate touchDelegate = new TouchDelegate(touchRect, menuIcon);

            View parentView = (View) menuIcon.getParent();
            parentView.setTouchDelegate(touchDelegate);
        });
        TextView tableNumber = view.findViewById(R.id.order_table_number);
        String tableNumbertext = "Tavolo n° " + sharedViewModel.getTableNumberMutableLiveData().getValue();
        tableNumber.setText(tableNumbertext);
    }

    private void removeStyle(MenuItem menuItem) {
        SpannableString spannableString = new SpannableString(menuItem.getTitle());
        spannableString.removeSpan(new StyleSpan(android.graphics.Typeface.BOLD));
        menuItem.setTitle(spannableString);
    }

    private void addStyle(MenuItem menuItem) {
        SpannableString spannableString = new SpannableString(menuItem.getTitle());
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,
                spannableString.length(), 0);
        menuItem.setTitle(spannableString);
    }

    private Spinner createSpinner() {
        Spinner spinner = new Spinner(requireContext());
        String[] items = new String[sharedViewModel.getPersonsMutableLiveData().size()+1];
        items[0] = "----------";
        for(int i = 1; i < items.length; i++) {
            items[i] = sharedViewModel.getPersonsMutableLiveData().get(i-1).getValue().getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void onClickAddButton(DrinkType drinkType, String name, String description, double price,
                                  Spinner spinner) {
        if(!spinner.getSelectedItem().toString().equals("----------")) {
            Drink drink = new Drink(drinkType, name, description, price);
            Person person = sharedViewModel.getPersonByName(spinner.getSelectedItem().toString());
            if(person != null) {
                OrderElement orderElement = new OrderElement(drink,person.getName());
                sharedViewModel.addElementInOrderMutableLiveData(orderElement);
            } else {
                Toast.makeText(requireContext(), R.string.name_invalid, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), R.string.who_drink, Toast.LENGTH_SHORT).show();
        }
    }

    private void createMenu(String drink, String nameHead, DrinkType drinkType) {
        TableLayout tableLayout = requireView().findViewById(R.id.order_table);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("menu");
        databaseReference.child(drink);

        //Creazione dell'intestazione della tabella
        TableRow row = new TableRow(requireContext());

        TextView nameTextView = new TextView(requireContext());
        nameTextView.setText(nameHead);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        nameTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(nameTextView);


        TextView priceTextView = new TextView(requireContext());
        priceTextView.setText(R.string.price);
        priceTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        priceTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(priceTextView);

        TextView spinnerTextView = new TextView(requireContext());
        spinnerTextView.setText(R.string.persons_spinner);
        spinnerTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        spinnerTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(spinnerTextView);

        TextView addTextView = new TextView(requireContext());
        addTextView.setText(R.string.add);
        addTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        addTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        row.addView(addTextView);

        tableLayout.addView(row);
        //lascio una riga vuota
        row = new TableRow(requireContext());
        tableLayout.addView(row);

        //Si inserisce nella tabella tutte le birre presenti nel real time database
        databaseReference.child(drink).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                for (DataSnapshot drinkSnapshot : dataSnapshot.getChildren()) {
                    String drinkName = drinkSnapshot.getKey();
                    String drinkDescription = drinkSnapshot.child("description").getValue(String.class);
                    double drinkPrice = drinkSnapshot.child("price").getValue(Double.class);

                    // Crea una nuova riga per la tabella beer_table
                    TableRow row = new TableRow(requireContext());

                    //Definizione del valore della cella name
                    TextView nameTextView = new TextView(requireContext());
                    nameTextView.setText(drinkName);
                    nameTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f));
                    nameTextView.setGravity(Gravity.CENTER_VERTICAL);
                    row.addView(nameTextView);

                    //Definizione del valore della cella price
                    TextView priceTextView = new TextView(requireContext());
                    priceTextView.setText(decimalFormat.format(drinkPrice));
                    priceTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f));
                    priceTextView.setGravity(Gravity.CENTER_VERTICAL);
                    row.addView(priceTextView);

                    //Aggiungo alla tabella lo spinner creato
                    Spinner spinner = createSpinner();
                    row.addView(spinner);

                    //Definizione del bottone per poter aggiungere la birra agli ordini
                    FrameLayout addButtonLayout = new FrameLayout(requireContext());
                    addButtonLayout.setId(View.generateViewId());
                    ImageView addButtonImage = new ImageView(requireContext());
                    addButtonImage.setImageResource(R.drawable.add_image);
                    addButtonImage.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER
                    ));

                    // Impostazione dell'evento onClickListener per il bottone
                    addButtonImage.setOnClickListener(v-> onClickAddButton(drinkType,
                            drinkName,drinkDescription,drinkPrice,spinner));

                    addButtonLayout.addView(addButtonImage);
                    addButtonLayout.setLayoutParams(new TableRow.LayoutParams(0,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    row.addView(addButtonLayout);

                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), R.string.read_data_error, Toast.LENGTH_SHORT).show();
                Log.d("OrderFragmentError", "Errore nella lettura dei dati");
                sharedViewModel.resetSharedViewModel();
                startActivity(new Intent(requireContext(), ErrorActivity.class));
            }
        });
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
    }

}