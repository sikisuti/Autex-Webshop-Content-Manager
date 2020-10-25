package org.autex.supplyer;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class Supplier {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Supplier.class);

    public Writer convert(InputStream... inputStream) throws IOException {
        return stringify(build(inputStream));
    }

    protected abstract List<String[]> build(InputStream... inputStream) throws IOException;

    private StringWriter stringify(List<String[]> content) throws IOException {
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            csvWriter.writeAll(content, false);
            return stringWriter;
        }
    }

    protected List<String[]> getTemplate() {
        List<String[]> template = new ArrayList<>();
        template.add(HEADERS);
        return template;
    }

    protected String[] getRowTemplate(int size) {
        String[] row = new String[size];
        row[1] = "simple";
        row[4] = row[22] = "1";
        row[5] = row[16] = row[17] = row[38] = "0";
        row[6] = "visible";
        row[11] = "taxable";
        return row;
    }

    private static final String[] HEADERS = new String[]{
            "Azonosító",
            "Típus",
            "Cikkszám",
            "Név",
            "Közzétéve",
            "Kiemelt?",
            "Látható a katalógusban",
            "Rövid leírás",
            "Leírás",
            "Akciós ár kezdődátuma",
            "Akciós ár végdátuma",
            "Adó státusz",
            "Adózási osztály",
            "Raktáron?",
            "Készlet",
            "Alacsony készlet mennyiség",
            "Függő rendelés engedélyezése?",
            "Egyedileg értékesítető?",
            "Tömeg (kg)",
            "Hosszúság (mm)",
            "Szélesség (mm)",
            "Magasság (mm)",
            "Engedélyezzük az értékelést?",
            "Vásárlási megjegyzés",
            "Akciós ár",
            "Normál ár",
            "Kategória",
            "Címkék",
            "Szállítási osztály",
            "Képek",
            "Letöltési korlát",
            "Letöltés lejárati napok",
            "Szülő",
            "Csoportosított termékek",
            "Upsell",
            "Keresztértékesítés",
            "Külső URL",
            "Gomb szövege",
            "Pozíció",
            "Meta: _measurement_1",
            "Meta: _measurement_2",
            "Meta: _measurement_3",
            "Meta: _inner_diameter",
            "Meta: _outer_diameter",
            "Meta: _width_2",
            "Meta: _netweight",
            "Meta: _grossweight",
            "Meta: _brand",
            "Meta: _suppliername",
            "Meta: _gws_efp_meta_text",
            "Tulajdonság neve 1",
            "Tulajdonság (1) értéke(i)",
            "Tulajdonság 1 láthatósága",
            "Tulajdonság (1) globális"
    };
}
