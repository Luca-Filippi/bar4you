package it.units.inginf.sim.bar4you;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class Bill {

    private final int tableNumber;
    private final String date;
    private final String hour;
    private final double billValue;

    public Bill(int tableNumber, double billValue) {
        this.tableNumber = tableNumber;
        // Ottieni la data
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.date = dateFormat.format(currentDate);
        // Ottieni ora
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        this.hour = timeFormat.format(currentDate);
        this.billValue = billValue;
    }

    public int getTableNumber() {
        return this.tableNumber;
    }

    public String getDate() {
        return this.date;
    }

    public String getHour() {
        return hour;
    }

    public double getBillValue() {
        return this.billValue;
    }
}
