package cz.jj.sotoradypce;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sotoradypce.db";
    public static final String TABLE_NAME = "spojepce";
    public static final String TABLE_NAME2 = "zastavkypce";
    public static final String TABLE_NAME3 = "odjezdypce";

    private Context mCtx;

    public DatabaseHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, 71);
        mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id_spoje INTEGER PRIMARY KEY, kurz INTEGER, linka TEXT, vychozi_zast_id INTEGER, vychozi_zast_cas TIME, konecna_zast_id INTEGER, konecna_zast_cas TIME, datumova_poznamka TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME3 + " (id_odjezdu INTEGER PRIMARY KEY, id_spoje INTEGER, id_zastavky INTEGER, cas_odjezdu TIME)");
        db.execSQL("CREATE TABLE " + TABLE_NAME2 + " (id INTEGER PRIMARY KEY, nazev TEXT)");

        //nacteni dat z textaku do databaze
        BufferedReader reader = null;
        String tables[] = {"spoje", "odjezdy", "zastavky"};
        String files[] = {"spoje.xxt", "odjezdy.xxt", "zastavky.xxt"};
        for (int i = 0; i < files.length; i++) {
            try {
                AssetManager am = mCtx.getAssets();
                reader = new BufferedReader(new InputStreamReader(am.open(files[i])));
                while ((tables[i] = reader.readLine()) != null) {
                    db.execSQL(tables[i]);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    public Cursor getAllData(String kurz, String den) {
        SQLiteDatabase db = this.getWritableDatabase();

        switch (den){
            case "5":
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE (datumova_poznamka = '" + den + "' OR datumova_poznamka = 'X') AND kurz =" + kurz + " ORDER BY vychozi_zast_cas", null);
            case "X":
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE (datumova_poznamka = 14 OR datumova_poznamka = 'X') AND kurz =" + kurz + " ORDER BY vychozi_zast_cas", null);
            default:
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE datumova_poznamka = '" + den + "' AND kurz =" + kurz + " ORDER BY vychozi_zast_cas", null);
        }

    }

    public Cursor getAllDataLine(String linka, String den) {
        SQLiteDatabase db = this.getWritableDatabase();
        switch (den) {
            case "5":
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE (datumova_poznamka = '" + den + "' OR datumova_poznamka = 'X') AND linka =" + linka + " ORDER BY vychozi_zast_cas", null);
            case "X":
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE (datumova_poznamka = 14 OR datumova_poznamka = 'X') AND linka =" + linka + " ORDER BY vychozi_zast_cas", null);
            default:
                return db.rawQuery("SELECT kurz, linka, z.nazev, strftime('%H:%M', vychozi_zast_cas), y.nazev, strftime('%H:%M', konecna_zast_cas) FROM " + TABLE_NAME + " INNER JOIN " + TABLE_NAME2 + " z ON (spojepce.vychozi_zast_id = z.id) INNER JOIN " + TABLE_NAME2 + " y ON (spojepce.konecna_zast_id = y.id) WHERE datumova_poznamka = '" + den + "' AND linka =" + linka + " ORDER BY vychozi_zast_cas", null);
        }
    }

    public Cursor getAllOdjezdy(String den, String id_zastavky, String linka, String cas) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (linka.isEmpty()) {
            switch (den) {
                case "5":
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE z.nazev ='" + id_zastavky + "' AND (datumova_poznamka = '" + den + "' OR datumova_poznamka = 'X') AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
                case "X":
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE z.nazev ='" + id_zastavky + "' AND (datumova_poznamka = 14 OR datumova_poznamka = 'X') AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
                default:
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE z.nazev ='" + id_zastavky + "' AND datumova_poznamka ='" + den + "' AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
            }
        } else {
            switch (den) {
                case "5":
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE linka =" + linka + " AND z.nazev ='" + id_zastavky + "' AND (datumova_poznamka = '" + den + "' OR datumova_poznamka = 'X') AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
                case "X":
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE linka =" + linka + " AND z.nazev ='" + id_zastavky + "' AND (datumova_poznamka = 14 OR datumova_poznamka = 'X') AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
                default:
                    return db.rawQuery("SELECT z.nazev, kurz, linka, strftime('%H:%M', cas_odjezdu), c.nazev, strftime('%H:%M', spojepce.konecna_zast_cas) AS kzc FROM " + TABLE_NAME + " JOIN " + TABLE_NAME3 + " ON (spojepce.id_spoje = odjezdypce.id_spoje) INNER JOIN " + TABLE_NAME2 + " z ON (odjezdypce.id_zastavky = z.id) INNER JOIN " + TABLE_NAME2 + " c ON (spojepce.konecna_zast_id = c.id) WHERE linka =" + linka + " AND z.nazev ='" + id_zastavky + "' AND datumova_poznamka ='" + den + "' AND cas_odjezdu >='" + cas + "' ORDER BY cas_odjezdu", null);
            }
        }
    }
}
