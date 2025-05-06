package VISTA;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
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
    DefaultTableModel ml = new DefaultTableModel(new Object[][]{}, new String[]{"ORIGEN", "SIMBOLO", "DESTINO"});
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
        //CONFIGURACION TABLE1
        JTableHeader header = table1.getTableHeader();
        header.setBackground(Color.decode("#002E40"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Consolas", Font.BOLD, 14));
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        table1.setModel(ml);


        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (estadoprograma){
                    case 0:
                        if (!inicial.getText().equals("") && !finall.getText().equals("")) {
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
                        if (!Origen.getText().isEmpty() && !Destino.getText().isEmpty()) {
                            ArrayList<String> vector = new ArrayList<>();
                            String aux;
                            vector.add(getOrigen());
                            if (Simbolo.getText().isEmpty()) {
                                vector.add("λ");
                                aux = "λ";
                            } else {
                                vector.add(Simbolo.getText());
                                aux = Simbolo.getText();
                            }
                            vector.add(getDestino());
                            agregarFilaATabla(getOrigen(), aux, getDestino());
                            Transiciones.add(vector);
                            System.out.println(Transiciones);
                        }else {
                            JOptionPane.showMessageDialog(vista.this, "FALTAN DATOS");
                        }
                        break;
                }
            }
        });
        crearAFButton.addActionListener(e -> {
            Set<String> estadosFinales = new HashSet<>(Arrays.asList(EstadoFinal.split(" ")));
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

            JTableHeader header2 = table2.getTableHeader();
            header2.setBackground(Color.decode("#661C16"));
            header2.setForeground(Color.WHITE);
            header2.setFont(new Font("Consolas", Font.BOLD, 14));
            DefaultTableCellRenderer renderer2 = (DefaultTableCellRenderer) header2.getDefaultRenderer();
            renderer2.setHorizontalAlignment(SwingConstants.CENTER);
            renderer2.setVerticalAlignment(SwingConstants.CENTER);


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
        agregarButton.setForeground(Color.WHITE);
        crearAFButton.setForeground(Color.WHITE);
        ConvertirAFDButton.setForeground(Color.WHITE);
        MinimizarAFDButton.setForeground(Color.WHITE);
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