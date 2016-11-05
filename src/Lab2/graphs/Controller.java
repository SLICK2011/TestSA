package Lab2.graphs;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class Controller {


    @FXML public BorderPane borderPaneMain;

    @FXML public TabPane tabPaneMain;
    @FXML public Tab[] tabs;
    @FXML public BorderPane[] borderPanes;
    @FXML public ComboBox[] comboBoxs;
    @FXML public LineChart<Number,Number>[] yLineChart;

    @FXML
    public void initialize() {

        tabPaneMain = new TabPane();
        borderPaneMain.setCenter(tabPaneMain);

        tabs = new Tab[Model.yi.length];
        borderPanes = new BorderPane[Model.yi.length];
        comboBoxs = new ComboBox[Model.yi.length];
        yLineChart = new LineChart[Model.yi.length];

        borderPaneMain.setCenter(tabPaneMain);
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "1.Нормовані значення",
                        "2.Відтворені значення"
                );

        for (int i = 0; i < Model.yi.length; i++) {
            comboBoxs[i] = new ComboBox();
            borderPanes[i] = new BorderPane();
            tabs[i] = new Tab(String.valueOf(i),borderPanes[i]);
            comboBoxs[i].getItems().addAll(options);

            borderPanes[i].setBottom(comboBoxs[i]);
            comboBoxs[i].getSelectionModel().select(0);
        }
        tabPaneMain.getTabs().addAll(tabs);

        for (int i = 0; i < Model.yi.length; i++) {
            EventHandler eh = new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    refresh();
                }
            };
            comboBoxs[i].setOnAction(eh);
            tabs[i].setOnSelectionChanged(eh);

        }


        tabSelected(0);
    }

    @FXML
    public void refresh(){
        for (int i = 0; i < Model.yi.length; i++) {
            if (tabs[i].isSelected()) tabSelected(i);
        }

    }


    @FXML
    public void tabSelected(int i){
        //System.out.println(comboBoxs[i].getSelectionModel().getSelectedIndex());
        if (comboBoxs[i].getSelectionModel().getSelectedIndex() == 0)
        {
            NumberAxis x = new NumberAxis();
            NumberAxis y = new NumberAxis();

            yLineChart[i] = new LineChart<Number, Number>(x,y);



            XYChart.Series series = new XYChart.Series();
            XYChart.Series series1 = new XYChart.Series();

            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
            ObservableList<XYChart.Data> datas1 = FXCollections.observableArrayList();

            for(int j=0; j<Model.yi[i].getNormaY().getRowDimension(); j++){
                datas.add(new XYChart.Data(j,Model.yi[i].getNormaY().get(j,0)));
               // datas.add(new XYChart.Data(j,j*j));
            }
            for(int j=0; j<Model.yi[i].getFindFnorma().getRowDimension(); j++){
                datas1.add(new XYChart.Data(j,Model.yi[i].getFindFnorma().get(j,0)));
            }

            series.setData(datas);
            series1.setData(datas1);


            borderPanes[i].setCenter(yLineChart[i]);
            yLineChart[i].getData().add(series);
            yLineChart[i].getData().add(series1);

        } else if (comboBoxs[i].getSelectionModel().getSelectedIndex() == 1){
            NumberAxis x = new NumberAxis();
            NumberAxis y = new NumberAxis();

            yLineChart[i] = new LineChart<Number, Number>(x,y);



            XYChart.Series series = new XYChart.Series();
            XYChart.Series series1 = new XYChart.Series();

            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
            ObservableList<XYChart.Data> datas1 = FXCollections.observableArrayList();

            for(int j=0; j<Model.yi[i].getOriginalY().getRowDimension(); j++){
                datas.add(new XYChart.Data(j,Model.yi[i].getOriginalY().get(j,0)));
            }
            for(int j=0; j<Model.yi[i].getFindForiginal().getRowDimension(); j++){
                datas1.add(new XYChart.Data(j,Model.yi[i].getFindForiginal().get(j,0)));
            }

            series.setData(datas);
            series1.setData(datas1);


            borderPanes[i].setCenter(yLineChart[i]);
            yLineChart[i].getData().add(series);
            yLineChart[i].getData().add(series1);
        }
    }



}
