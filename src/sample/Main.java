package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    public static int updatePrice(String usageName, String price, String period,String fixedUnit) throws IOException {
        if ( usageName.isEmpty()|| price.isEmpty()|| period.isEmpty()|| fixedUnit.isEmpty())
            return -2;
        BufferedReader file = new BufferedReader(new FileReader("ProductCatalogPreTariffs.xml"));
        PrintWriter writer = new PrintWriter(new File("ProductCatalogPreTariffs.AUT.xml"), "UTF-8");
        String line;

        boolean firstLine=true;
        int modifiedLines = 0;
        boolean inside = false, insidePrice = false;
        String pattern = "^\\s*<usageRules id=\"[^\"]+\" name=\"" + usageName + "\">";
        Pattern r = Pattern.compile(pattern);
        Matcher m;
        String patternKapa = "^\\s*</usageRules>";
        Pattern rKapa = Pattern.compile(patternKapa);
        Matcher mKapa;
        String patternPrice = "^\\s*<prices id=\"[^\"]+\">";
        Pattern rPrice = Pattern.compile(patternPrice);
        Matcher mPrice;

        String patternPriceKapa = "^\\s*</prices>";
        Pattern rPriceKapa = Pattern.compile(patternPriceKapa);
        Matcher mPriceKapa;
        while ((line = file.readLine()) != null) {
            m = r.matcher(line);

            if (m.find())
                inside = true;
            else {
                mKapa = rKapa.matcher(line);
                if (mKapa.find())
                    inside = false;
            }
            if (inside) {
                mPrice = rPrice.matcher(line);
                mPriceKapa = rPrice.matcher(line);
                if (mPrice.find())
                    insidePrice = true;
                else if (mPriceKapa.find())
                    insidePrice = false;
                //Strict checking for AUT-level safety
                if (insidePrice) {
                    String line0 = line;
                    line = line.replaceAll("<costPerUnit>[^<]*</costPerUnit>", "<costPerUnit>" + price + "</costPerUnit>").replaceAll("<unitSize>[^<]*</unitSize>", "<unitSize>" + period + "</unitSize>").
                    replaceAll("<fixedUnit>[^<]*</fixedUnit>", "<fixedUnit>" + fixedUnit + "</fixedUnit>");
                    if (!line.contentEquals(line0))
                        ++modifiedLines;
                }

            }

            if (!firstLine){
                line = System.getProperty("line.separator")+line;

            }
            else
                firstLine=false;

            writer.print(line);

        }
        file.close();
        if (writer.checkError())
            throw new IOException("cannot write");
        writer.close();
        return modifiedLines;
    }

    private int counter = 0;

    public static void main(String[] args) {
        launch();
    }

    @SuppressWarnings("static-access")
    public void start(Stage stage) {
        stage.setTitle("Price Updater by AUT");

        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);

        Rectangle r1 = new Rectangle();
        r1.setFill(Color.DARKKHAKI);
        r1.setHeight(100);
        r1.setWidth(500);

        Rectangle r2 = new Rectangle();
        r2.setFill(Color.rgb(189, 40, 40));
        r2.setHeight(100);
        r2.setWidth(500);

        Rectangle r3 = new Rectangle();
        r3.setFill(Color.hsb(235, 0.52, 0.36));
        r3.setHeight(100);
        r3.setWidth(500);

        Rectangle r4 = new Rectangle();
        r4.setFill(Color.web("b894cc"));
        r4.setHeight(100);
        r4.setWidth(500);

        grid.add(r1, 0, 0);
        grid.add(r2, 0, 1);
        grid.add(r3, 1, 0);
        grid.add(r4, 1, 1);

        Label l = new Label();
        l.setFont(new Font("Calibri", 15));
        l.setTextFill(Color.BLACK);

        Button button = new Button();
        button.setFont(new Font("Calibri", 15));
        button.setText("    UPDATE !   ");


        Label label1 = new Label("Usage Rule Name:");
        TextField textField = new TextField();

        Label label2 = new Label("Price:");
        TextField textField2 = new TextField();

        Label label3 = new Label("Charging Period:");
        label3.setTextFill(Color.BEIGE);
        TextField textField3 = new TextField();
       Label label4 = new Label("fixedUnit:");
        label4.setTextFill(Color.BEIGE);
        TextField textField4 = new TextField();
        HBox hb3 = new HBox();
        hb3.getChildren().addAll(label1, textField);
        hb3.getChildren().addAll(label2, textField2);
        hb3.getChildren().addAll(label3, textField3);
        hb3.getChildren().addAll(label4, textField4);
        hb3.setSpacing(10);

        BorderPane bp = new BorderPane();
        bp.setBottom(l);
        bp.setAlignment(l, Pos.CENTER);
        bp.setCenter(button);
        bp.setTop(hb3);


        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               // ++counter;

                int lines= 0;
                try {
                    lines = updatePrice(textField.getText(), textField2.getText(), textField3.getText(), textField4.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                    lines=-1;
                }
                l.setText("Changed "+lines + ((lines == -1) ? " EXCEPTION ALDI." : " lines"));
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(grid);
        root.getChildren().add(bp);
        stage.setScene(new Scene(root, 1000, 200));
        stage.show();

    }


}