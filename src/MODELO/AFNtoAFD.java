package MODELO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

public class AFNtoAFD {
    private final String simboloLambda;
    private Map<String, Map<String, Set<String>>> afn = new HashMap<>();
    private Set<String> todosEstados = new HashSet<>();
    private Set<String> estadosFinales = new HashSet<>();
    private String estadoInicial;
    private Set<String> alfabeto = new HashSet<>();
    private Map<String, Map<String, String>> afd = new HashMap<>();
    private Map<String, Map<String, String>> afdm;
    private Set<String> estadosFinalesAFD = new HashSet<>();

    public AFNtoAFD(String estadoInicial, Set<String> estadosFinales, List<List<String>> transiciones, String simboloLambda) {
        this.estadoInicial = estadoInicial;
        this.simboloLambda = simboloLambda;
        this.estadosFinales.addAll(estadosFinales);
        todosEstados.add(estadoInicial);
        todosEstados.addAll(estadosFinales);

        for (List<String> t : transiciones) {
            String origen = t.get(0);
            String simbolo = t.get(1);
            String destino = t.get(2);
            afn.computeIfAbsent(origen, k -> new HashMap<>())
                    .computeIfAbsent(simbolo, k -> new HashSet<>())
                    .add(destino);
            todosEstados.add(origen);
            todosEstados.add(destino);
        }

        alfabeto = afn.values().stream()
                .flatMap(map -> map.keySet().stream())
                .filter(s -> !s.equals(simboloLambda))
                .collect(Collectors.toSet());
    }

    public AFNtoAFD(String estadoInicial, Set<String> estadosFinales, List<List<String>> transiciones) {
        this(estadoInicial, estadosFinales, transiciones, "λ");
    }

    private Set<String> cerraduraLambda(String estado) {
        Set<String> resultado = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        resultado.add(estado);
        q.add(estado);
        while (!q.isEmpty()) {
            String actual = q.poll();
            Set<String> siguientes = afn.getOrDefault(actual, new HashMap<>()).get(simboloLambda);
            if (siguientes != null) {
                for (String siguiente : siguientes) {
                    if (!resultado.contains(siguiente)) {
                        resultado.add(siguiente);
                        q.add(siguiente);
                    }
                }
            }
        }
        return resultado;
    }

    private Set<String> cerraduraLambdaConjunto(Set<String> conjunto) {
        Set<String> resultado = new HashSet<>();
        for (String e : conjunto) {
            resultado.addAll(cerraduraLambda(e));
        }
        return resultado;
    }

    private Set<String> mover(Set<String> estados, String simbolo) {
        Set<String> resultado = new HashSet<>();
        for (String e : estados) {
            Set<String> destinos = afn.getOrDefault(e, new HashMap<>()).get(simbolo);
            if (destinos != null) {
                resultado.addAll(destinos);
            }
        }
        return resultado;
    }

    private String nombreConjunto(Set<String> conjunto) {
        List<String> ordenado = new ArrayList<>(conjunto);
        Collections.sort(ordenado);
        return "{" + String.join(",", ordenado) + "}";
    }

    public void generarAFD() {
        Queue<Set<String>> pendientes = new LinkedList<>();
        Set<String> vistos = new HashSet<>();

        Set<String> inicio = cerraduraLambda(estadoInicial);
        pendientes.add(inicio);
        vistos.add(nombreConjunto(inicio));

        while (!pendientes.isEmpty()) {
            Set<String> actual = pendientes.poll();
            String nombreActual = nombreConjunto(actual);

            for (String a : alfabeto) {
                Set<String> mov = mover(actual, a);
                Set<String> cierre = cerraduraLambdaConjunto(mov);
                if (!cierre.isEmpty()) {
                    String nombreCierre = nombreConjunto(cierre);
                    afd.computeIfAbsent(nombreActual, k -> new HashMap<>()).put(a, nombreCierre);
                    if (!vistos.contains(nombreCierre)) {
                        vistos.add(nombreCierre);
                        pendientes.add(cierre);
                    }
                }
            }
        }

        for (String nombreEstado : vistos) {
            for (String f : estadosFinales) {
                if (nombreEstado.contains(f)) {
                    estadosFinalesAFD.add(nombreEstado);
                    break;
                }
            }
        }

        afdm = minimizarAFD(afd, alfabeto, nombreConjunto(inicio), estadosFinalesAFD);
    }

    private Map<String, Map<String, String>> minimizarAFD(
            Map<String, Map<String, String>> afd,
            Set<String> alfabeto,
            String estadoInicial,
            Set<String> estadosFinalesAFD) {

        Set<String> estadosAFD = new HashSet<>(afd.keySet());
        List<Set<String>> particion = new ArrayList<>();
        Set<String> noFinales = new HashSet<>(estadosAFD);
        noFinales.removeAll(estadosFinalesAFD);

        if (!noFinales.isEmpty()) particion.add(noFinales);
        if (!estadosFinalesAFD.isEmpty()) particion.add(estadosFinalesAFD);

        boolean cambiado;
        do {
            cambiado = false;
            List<Set<String>> nuevaParticion = new ArrayList<>();
            for (Set<String> grupo : particion) {
                Map<String, Set<String>> divisiones = new HashMap<>();
                for (String estado : grupo) {
                    StringBuilder clave = new StringBuilder();
                    for (String s : alfabeto) {
                        String destino = afd.getOrDefault(estado, new HashMap<>()).getOrDefault(s, "-");
                        int claseDestino = -1;
                        for (int i = 0; i < particion.size(); i++) {
                            if (particion.get(i).contains(destino)) {
                                claseDestino = i;
                                break;
                            }
                        }
                        clave.append(claseDestino).append(",");
                    }
                    divisiones.computeIfAbsent(clave.toString(), k -> new HashSet<>()).add(estado);
                }
                nuevaParticion.addAll(divisiones.values());
                if (divisiones.size() > 1) cambiado = true;
            }
            particion = nuevaParticion;
        } while (cambiado);

        Map<String, String> estadoAGrupo = new HashMap<>();
        for (Set<String> grupo : particion) {
            String nombre = grupo.iterator().next();
            for (String estado : grupo) {
                estadoAGrupo.put(estado, nombre);
            }
        }

        Map<String, Map<String, String>> nuevoAFD = new HashMap<>();
        for (Set<String> grupo : particion) {
            String represent = grupo.iterator().next();
            for (String s : alfabeto) {
                String destino = afd.getOrDefault(represent, new HashMap<>()).get(s);
                if (destino != null && !destino.equals("-")) {
                    nuevoAFD.computeIfAbsent(represent, k -> new HashMap<>())
                            .put(s, estadoAGrupo.get(destino));
                }
            }
        }

        return nuevoAFD;
    }

    public List<List<String>> obtenerTablaAFD() {
        return construirTabla(afd, alfabeto);
    }

    public List<List<String>> obtenerTablaAFDMinimizado() {
        return construirTabla(afdm, alfabeto);
    }

    private List<List<String>> construirTabla(Map<String, Map<String, String>> tabla, Set<String> alfabeto) {
        List<List<String>> resultado = new ArrayList<>();
        List<String> encabezado = new ArrayList<>();
        encabezado.add("Estado");
        encabezado.addAll(alfabeto);
        resultado.add(encabezado);

        for (Map.Entry<String, Map<String, String>> fila : tabla.entrySet()) {
            List<String> filaDatos = new ArrayList<>();
            filaDatos.add(fila.getKey());
            for (String simbolo : alfabeto) {
                filaDatos.add(fila.getValue().getOrDefault(simbolo, "-"));
            }
            resultado.add(filaDatos);
        }
        return resultado;
    }
}