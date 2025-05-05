package VISTA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import MODELO.AFNtoAFD;

public class vista extends javax.swing.JFrame {
    static String EstadoInicial;
    static String EstadoFinal;
    DefaultTableModel ml = new DefaultTableModel();
    List<List<String>> Transiciones = new ArrayList<>();
    List<List<String>> tablaAFD = new ArrayList<>();
    List<List<String>> tablaAFDM = new ArrayList<>();

    private JPanel PANELPRINCIPAL;
    private JTable table1;
    private JButton agregarButton;
    private JButton crearAFButton;
    private JTextField inicial;
    private JTextField finall;
    private JTextField Origen;
    private JTextField Simbolo;
    private JTextField Destino;
    private JButton ConvertirAFDButton;
    private JButton MinimizarAFDButton;
    private JTable table2;
    private JLabel texto;
    private int estadoprograma;
    public vista() {
        iniciar();
        ml.setColumnIdentifiers(new Object[]{"ORIGEN", "SIMBOLO", "DESTINO"});
        table1.setModel(ml);
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (estadoprograma){
                    case 0:
                        if (!inicial.getText().equals("") || !finall.getText().equals("")) {
                            EstadoInicial = getInicial();
                            EstadoFinal = getFinal();
                            crearAFButton.setEnabled(true);
                            Origen.setEnabled(true);
                            Simbolo.setEnabled(true);
                            Destino.setEnabled(true);
                            inicial.setText("");
                            inicial.setEnabled(false);
                            finall.setText("");
                            finall.setEnabled(false);
                            estadoprograma++;
                        } else {
                            JOptionPane.showMessageDialog(vista.this, "FALTAN DATOS");
                        }
                        break;
                    case 1:
                        if(!Origen.getText().equals("") || !Simbolo.getText().equals("")  || !Destino.getText().equals("")){
                            ArrayList<String> vector = new ArrayList<>();
                            vector.add(getOrigen());
                            vector.add(getSimbolo());
                            vector.add(getDestino());
                            agregarFilaATabla(getOrigen(),getSimbolo(),getDestino());
                            Transiciones.add(vector);
                            System.out.println(Transiciones);
                        }
                        break;
                }
            }
        });
        crearAFButton.addActionListener(e -> {
            Set<String> estadosFinales = new HashSet<>(Arrays.asList(EstadoFinal));
            AFNtoAFD automata = new AFNtoAFD(EstadoInicial, estadosFinales, Transiciones);
            automata.generarAFD();
            tablaAFD = automata.obtenerTablaAFD();
            tablaAFDM = automata.obtenerTablaAFDMinimizado();
            crearAFButton.setEnabled(false);
            ConvertirAFDButton.setEnabled(true);
            MinimizarAFDButton.setEnabled(true);
            Origen.setEnabled(false);
            Simbolo.setEnabled(false);
            Destino.setEnabled(false);
            agregarButton.setEnabled(false);
        });
        ConvertirAFDButton.addActionListener(e -> {
            DefaultTableModel modeloAFD = new DefaultTableModel();
            texto.setText("AFD");
            List<String> encabezados = tablaAFD.get(0);
            modeloAFD.setColumnIdentifiers(encabezados.toArray());

            for (int i = 1; i < tablaAFD.size(); i++) {
                List<String> fila = tablaAFD.get(i);
                modeloAFD.addRow(fila.toArray());
            }

            table2.setModel(modeloAFD);

            System.out.println("Mostrando tabla AFD en table2");
        });


        MinimizarAFDButton.addActionListener(e -> {
            DefaultTableModel modeloAFDM = new DefaultTableModel();
            texto.setText("AFDM");
            List<String> encabezados = tablaAFDM.get(0);
            modeloAFDM.setColumnIdentifiers(encabezados.toArray());

            for (int i = 1; i < tablaAFDM.size(); i++) {
                List<String> fila = tablaAFDM.get(i);
                modeloAFDM.addRow(fila.toArray());
            }

            table2.setModel(modeloAFDM);

            System.out.println("Mostrando tabla AFD Minimizado en table2");
        });


    }


    private void iniciar(){
        estadoprograma = 0;
        setContentPane(PANELPRINCIPAL);
        setTitle("VISTA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600);
        crearAFButton.setEnabled(false);
        ConvertirAFDButton.setEnabled(false);
        MinimizarAFDButton.setEnabled(false);
        Origen.setEnabled(false);
        Simbolo.setEnabled(false);
        Destino.setEnabled(false);
        setVisible(true);
    }

    public String getInicial() {
        return inicial.getText();
    }

    public String getFinal() {
        return finall.getText();
    }

    public String getOrigen() {
        return Origen.getText();
    }

    public String getSimbolo() {
        return Simbolo.getText();
    }

    public String getDestino() {
        return Destino.getText();
    }

    public void agregarFilaATabla(String desde, String simbolo, String hasta){
        ml.addRow(new String[]{desde, simbolo, hasta});
    }
}
