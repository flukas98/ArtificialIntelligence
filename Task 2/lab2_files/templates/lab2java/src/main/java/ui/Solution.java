package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Solution {
	public static void main(String ... args) throws IOException {
		String zastavicaZadatak = args[0];
		Path popisKlauzula_putanja = Paths.get(args[1]);
		Path korisnickeNaredbe_putanja = Paths.get("");;
		boolean verbose = false;
		if(args.length == 3) {
			if(args[2].equals("verbose")) {
				verbose = true;
			}
			else {
				korisnickeNaredbe_putanja = Paths.get(args[2]);
			}
		}
		else if(args.length == 4 && args[3].equals("verbose")) {
			korisnickeNaredbe_putanja = Paths.get(args[2]);
			verbose = true;
		}
		
		List<String> popisKlauzula_file = Files.readAllLines(popisKlauzula_putanja, StandardCharsets.UTF_8);
		
		List<String> popisKlauzula = new ArrayList<>();
		List<List<String>> popisKlauzulaPoLiteralima = new ArrayList<>();
		
		for(String klauzula_string : popisKlauzula_file) {
            if(klauzula_string.startsWith("#") == false) {
            	popisKlauzula.add(klauzula_string.toLowerCase());
            	List<String> literaliKlauzule = Arrays.asList(klauzula_string.toLowerCase().split(" v "));
            	popisKlauzulaPoLiteralima.add(literaliKlauzule);
            }
        }
		
		// PRVI ZADATAK
		if(zastavicaZadatak.equals("resolution")){
			List<String> popisPocetnihKlauzula = popisKlauzula.subList(0, popisKlauzula.size() - 1);
			String ciljnaKlauzula = popisKlauzula.get(popisKlauzula.size() - 1);
			
			List<List<String>> popisPocetnihKlauzulaPoLiteralima = popisKlauzulaPoLiteralima.subList(0, popisKlauzulaPoLiteralima.size() - 1);
			List<String> ciljnaKlauzulaPoLiteralima = popisKlauzulaPoLiteralima.get(popisKlauzulaPoLiteralima.size() - 1);
			
			// Uklanjanje nevaznih klauzula.
			for(int index : Distinct(IndexiValjanihKlauzula(popisPocetnihKlauzulaPoLiteralima))) {
				popisPocetnihKlauzulaPoLiteralima.remove(index);
				popisPocetnihKlauzula.remove(index);
			}
			// Uklanjanje redundantnih klauzula.
			for(int index : Distinct(IndexiRedundantnihKlauzula(popisPocetnihKlauzulaPoLiteralima))) {
				popisPocetnihKlauzulaPoLiteralima.remove(index);
				popisPocetnihKlauzula.remove(index);
			}
			
			
			//// Negirana ciljna klauzula.
			List<String> negiranaCiljnaKlauzulaPoLiteralima = new ArrayList<>();
            for(String ciljniLiteral : ciljnaKlauzulaPoLiteralima) {
                String suprotanLiteral = SuprotanLiteral(ciljniLiteral);
                negiranaCiljnaKlauzulaPoLiteralima.add(suprotanLiteral);
            }
			//// Skup potrpore (samo negirana ciljna klauzula na pocetku).
			List<String> skupPotpore = new ArrayList<>();
			List<List<String>> skupPotporePoLiteralima = new ArrayList<>();
            for (String ciljniLiteral : negiranaCiljnaKlauzulaPoLiteralima) {
                    skupPotpore.add(ciljniLiteral);
                    skupPotporePoLiteralima.add(Arrays.asList(ciljniLiteral));
            }
            
            PrintKlauzule(popisPocetnihKlauzula, negiranaCiljnaKlauzulaPoLiteralima);
            
            //// Logika.
            List<List<String>> sveKlauzulePoLiteralima = new ArrayList<>();
            sveKlauzulePoLiteralima.addAll(popisPocetnihKlauzulaPoLiteralima);
            sveKlauzulePoLiteralima.addAll(skupPotporePoLiteralima);
            List<Pair<Integer, Integer>> upareneKlauzule = new ArrayList<>();

            while (true) {
                Pair<Integer, Integer> indexi = PrimjenaPravila(sveKlauzulePoLiteralima, upareneKlauzule, popisPocetnihKlauzulaPoLiteralima.size());

                if (indexi == null) {
                	if(verbose == true) {                    	
                		String unknownString = ciljnaKlauzula + " is unknown";
                        System.out.println("=============");
                        System.out.println(unknownString);
                        System.out.println();
                    }
                	else {                    	
                		String unknownString = ciljnaKlauzula + " is unknown";
                        System.out.println(unknownString);
                    }
                    break;
                }


                List<String> klauzula1 = sveKlauzulePoLiteralima.get(indexi.getKey());
                List<String> klauzula2 = sveKlauzulePoLiteralima.get(indexi.getValue());

                List<String> literaliStarihKlauzula = new ArrayList<>();
                literaliStarihKlauzula.addAll(klauzula1);
                literaliStarihKlauzula.addAll(klauzula2);
                List<String> novaKlauzulaLiterali = new ArrayList<>();
                for (String literal : literaliStarihKlauzula) {
                    if (ListaSadrziString(literaliStarihKlauzula, SuprotanLiteral(literal)) == false) {
                        novaKlauzulaLiterali.add(literal);
                    }
                }
                if(literaliStarihKlauzula.size() - novaKlauzulaLiterali.size() == 2) {
                	if (novaKlauzulaLiterali.size() == 0) {
                		if(verbose == true) {                    	
                			String nilString = Integer.toString(sveKlauzulePoLiteralima.size() + 1) + ". " + "NIL" + '\t' + "(" + (indexi.getKey() + 1) + ", " + Integer.toString(indexi.getValue()+ 1) + ")";
                    		String trueString = ciljnaKlauzulaPoLiteralima.get(0) + " is true";
                    		System.out.println(nilString);
                    		System.out.println("=============");
                    		System.out.println(trueString);
                    		System.out.println();
                        }
                    	else {                    	
                    		String trueString = ciljnaKlauzulaPoLiteralima.get(0) + " is true";
                    		System.out.println(trueString);
                        }
                		
                		break;
                	}
                	else {
                		if(verbose == true) {
	                		String novaKlauzulaString = Integer.toString(sveKlauzulePoLiteralima.size() + 1) + ". " + String.join(" v ", novaKlauzulaLiterali) + '\t' + "(" + Integer.toString(indexi.getKey() + 1) + ", " + Integer.toString(indexi.getValue() + 1) + ")";
	                		System.out.println(novaKlauzulaString);
                		}
                	}                	
                	sveKlauzulePoLiteralima.add(novaKlauzulaLiterali);
                }
                upareneKlauzule.add(indexi);
            }
		}
		// DRUGI ZADATAK
		else if(zastavicaZadatak.equals("cooking_test") || zastavicaZadatak.equals("cooking_interactive")) {
			List<String> korisnickeNaredbe_file = new ArrayList<>();
			if(zastavicaZadatak.equals("cooking_test")) {
				korisnickeNaredbe_file = Files.readAllLines(korisnickeNaredbe_putanja, StandardCharsets.UTF_8);
			}
			List<String> popisPocetnihKlauzula = popisKlauzula.subList(0, popisKlauzula.size());
			List<List<String>> popisPocetnihKlauzulaPoLiteralima = popisKlauzulaPoLiteralima.subList(0, popisKlauzulaPoLiteralima.size());
			
			// Uklanjanje nevaznih klauzula.
			for(int index : Distinct(IndexiValjanihKlauzula(popisPocetnihKlauzulaPoLiteralima))) {
				popisPocetnihKlauzulaPoLiteralima.remove(index);
				popisPocetnihKlauzula.remove(index);
			}
			// Uklanjanje redundantnih klauzula.
			for(int index : Distinct(IndexiRedundantnihKlauzula(popisPocetnihKlauzulaPoLiteralima))) {
				popisPocetnihKlauzulaPoLiteralima.remove(index);
				popisPocetnihKlauzula.remove(index);
			}
			
			if(verbose == true) {
				System.out.println("Testing cooking assistant with standard resolution constructed with knowledge:");
				for(String pocetnaKlauzula : popisPocetnihKlauzula) {
					System.out.println("> " + pocetnaKlauzula);
				}				
			}
			
			int linija = 0;
			while(true) {
				String input = "";
				if(zastavicaZadatak.equals("cooking_test")) {
					input = korisnickeNaredbe_file.get(linija);
					linija ++;
				}
				else if(zastavicaZadatak.equals("cooking_interactive")) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));   
					System.out.println("Please enter your query");
					input = reader.readLine();					
				}
                List<String> inputList = Arrays.asList(input.split(" "));
                if (input.equals("exit")) {
                    break;
                }
                else if (inputList.get(inputList.size() - 1).equals("+")) {
                	List<String> novaKlauzula = inputList.subList(0, inputList.size() - 1);
                    String novaKlauzulaStringLowerCase = String.join(" ", novaKlauzula);
                    
                    String malaSlova = novaKlauzulaStringLowerCase.toLowerCase();
                    List<String> novaKlauzulaPoLiteralima = Arrays.asList(malaSlova.split(" v "));
                    
                    popisPocetnihKlauzulaPoLiteralima.add(novaKlauzulaPoLiteralima);
                    popisPocetnihKlauzula.add(malaSlova);
                }
                else if (inputList.get(inputList.size() - 1).equals("-")) {
                	List<String> novaKlauzula = inputList.subList(0, inputList.size() - 1);
                	String novaKlauzulaString = String.join(" ", novaKlauzula);
                	
            		String novaKlauzulaStringLowerCase = novaKlauzulaString.toLowerCase();
                    List<String> novaKlauzulaPoLiteralima = Arrays.asList(novaKlauzulaStringLowerCase.split(" v "));
                    
                    Pair<Integer, Integer> provjera = ListaSadrziKlauzulu(popisPocetnihKlauzulaPoLiteralima, novaKlauzulaPoLiteralima);
                    int index = provjera.getValue();
                    if (provjera.getKey() == 1) {
                    	popisPocetnihKlauzulaPoLiteralima.remove(index);
                    	popisPocetnihKlauzula.remove(index);
                    }
                }
                else if (inputList.get(inputList.size() - 1).equals("?")) {
                	List<String> novaKlauzula = inputList.subList(0, inputList.size() - 1);
                	String novaKlauzulaString = String.join(" ", novaKlauzula);
                	
            		String novaKlauzulaStringLowerCase = novaKlauzulaString.toLowerCase();
                    List<String> novaKlauzulaPoLiteralima = Arrays.asList(novaKlauzulaStringLowerCase.split(" v "));
                    
                    //// Negirana ciljna klauzula.
        			List<String> negiranaCiljnaKlauzulaPoLiteralima = new ArrayList<>();
                    for(String ciljniLiteral : novaKlauzulaPoLiteralima) {
                        String suprotanLiteral = SuprotanLiteral(ciljniLiteral);
                        negiranaCiljnaKlauzulaPoLiteralima.add(suprotanLiteral);
                    }
        			//// Skup potrpore (samo negirana ciljna klauzula na pocetku).
        			List<String> skupPotpore = new ArrayList<>();
        			List<List<String>> skupPotporePoLiteralima = new ArrayList<>();
                    for (String ciljniLiteral : negiranaCiljnaKlauzulaPoLiteralima) {
                            skupPotpore.add(ciljniLiteral);
                            skupPotporePoLiteralima.add(Arrays.asList(ciljniLiteral));
                    }
                    
                    if(verbose == true) {                    	
                    	PrintKlauzule(popisPocetnihKlauzula, negiranaCiljnaKlauzulaPoLiteralima);
                    }
                    
                    //// Logika.
                    List<List<String>> sveKlauzulePoLiteralima = new ArrayList<>();
                    sveKlauzulePoLiteralima.addAll(popisPocetnihKlauzulaPoLiteralima);
                    sveKlauzulePoLiteralima.addAll(skupPotporePoLiteralima);
                    List<Pair<Integer, Integer>> upareneKlauzule = new ArrayList<>();

                    while (true) {
                        Pair<Integer, Integer> indexi = PrimjenaPravila(sveKlauzulePoLiteralima, upareneKlauzule, popisPocetnihKlauzulaPoLiteralima.size());

                        if (indexi == null) {
                        	if(verbose == true) {                    	
                            	String unknownString = novaKlauzulaStringLowerCase + " is unknown";
                            	System.out.println("=============");
                            	System.out.println(unknownString);
                            	System.out.println();
                            }
                        	else {                    	
                            	String unknownString = novaKlauzulaStringLowerCase + " is unknown";
                            	System.out.println(unknownString);
                            }
                            break;
                        }


                        List<String> klauzula1 = sveKlauzulePoLiteralima.get(indexi.getKey());
                        List<String> klauzula2 = sveKlauzulePoLiteralima.get(indexi.getValue());

                        List<String> literaliStarihKlauzula = new ArrayList<>();
                        literaliStarihKlauzula.addAll(klauzula1);
                        literaliStarihKlauzula.addAll(klauzula2);
                        List<String> novaKlauzulaLiterali = new ArrayList<>();
                        for (String literal : literaliStarihKlauzula) {
                            if (ListaSadrziString(literaliStarihKlauzula, SuprotanLiteral(literal)) == false) {
                                novaKlauzulaLiterali.add(literal);
                            }
                        }
                        if(literaliStarihKlauzula.size() - novaKlauzulaLiterali.size() == 2) {
                        	if (novaKlauzulaLiterali.size() == 0) {
                        		if(verbose == true) {                    	
                        			String nilString = Integer.toString(sveKlauzulePoLiteralima.size() + 1) + ". " + "NIL" + '\t' + "(" + (indexi.getKey() + 1) + ", " + Integer.toString(indexi.getValue()+ 1) + ")";
                            		String trueString = novaKlauzulaStringLowerCase + " is true";
                            		System.out.println(nilString);
                            		System.out.println("=============");
                            		System.out.println(trueString);
                            		System.out.println();
                                }
                            	else {                    	
                            		String trueString = novaKlauzulaStringLowerCase + " is true";
                            		System.out.println(trueString);
                                }
                        		
                        		break;
                        	}
                        	else {
                        		if(verbose == true) {
                        			String izvedenaKlauzulaString = Integer.toString(sveKlauzulePoLiteralima.size() + 1) + ". " + String.join(" v ", novaKlauzulaLiterali) + '\t' + "(" + Integer.toString(indexi.getKey() + 1) + ", " + Integer.toString(indexi.getValue() + 1) + ")";
                        			System.out.println(izvedenaKlauzulaString);                        			
                        		}
                        	}                	
                        	sveKlauzulePoLiteralima.add(novaKlauzulaLiterali);
                        }
                        upareneKlauzule.add(indexi);
                    }
                    
                }
                else {
                	System.out.println("Nije podrzano");
                }
                
                if(linija == korisnickeNaredbe_file.size()) {
                	break;
                }
			}
		}
	}
	
	public static void PrintKlauzule(List<String> pocetneKlauzule, List<String> negiranaCiljnaKlauzula) {
        int i = 1;
        for (String klauzula : pocetneKlauzule) {
            String klauzulaPrint = Integer.toString(i) + ". " + klauzula;
            i++;
            System.out.println(klauzulaPrint);
        }
        System.out.println("=============");
        for (String ciljniLiteral : negiranaCiljnaKlauzula) {
            String klauzulaPrint = Integer.toString(i) + ". " + ciljniLiteral;
            i++;
            System.out.println(klauzulaPrint);
        }
        System.out.println("=============");
    }
	
	public static List<Integer> IndexiValjanihKlauzula(List<List<String>> popisKlauzulaPoLiteralima) {
		List<Integer> indexiValjanihKlauzula = new ArrayList<>();
		
		for(int i = 0; i < popisKlauzulaPoLiteralima.size(); i++) {
			List<String> literaliKlauzule = popisKlauzulaPoLiteralima.get(i);
			if(ProvjeraValjanostiKlauzule(literaliKlauzule)) {
				indexiValjanihKlauzula.add(i);
			}
		}
		
		return indexiValjanihKlauzula;
	}
	
	public static boolean ProvjeraValjanostiKlauzule(List<String> literaliKlauzule) {
		for (String literal : literaliKlauzule)
        {
            String suprotniLiteral = SuprotanLiteral(literal);
            if (ListaSadrziString(literaliKlauzule, literal) && ListaSadrziString(literaliKlauzule, suprotniLiteral))
            {
                return true;
            }
        }
        return false;
	}
	
	public static List<Integer> IndexiRedundantnihKlauzula(List<List<String>> popisKlauzulaPoLiteralima) {
		List<Integer> indexiRedundantnihKlauzula = new ArrayList<>();
		
		for(int i = 0; i < popisKlauzulaPoLiteralima.size(); i++) {
			List<String> literaliKlauzule1 = popisKlauzulaPoLiteralima.get(i);
            for (int j = 0; j < popisKlauzulaPoLiteralima.size(); j++) {
            	List<String> literaliKlauzule2 = popisKlauzulaPoLiteralima.get(j);
                if (i != j)
                {
                    if(literaliKlauzule1.size() == ProvjeraRedundantnostiKlauzule(literaliKlauzule1, literaliKlauzule2)) {
                    	indexiRedundantnihKlauzula.add(j);
                    }
                }
        	}
        }
		
		return indexiRedundantnihKlauzula;
	}
	
	public static int ProvjeraRedundantnostiKlauzule(List<String> literaliKlauzule1, List<String> literaliKlauzule2) {
		int podudarnost = 0;
		for(String literal : literaliKlauzule1)
        {
            if (ListaSadrziString(literaliKlauzule2, literal)) {
            	podudarnost++;
            }
        }
		
		return podudarnost;
	}
	
	public static String SuprotanLiteral(String literal)
    {
        if (literal.startsWith("~"))
        {
            return literal.substring(1);
        }
        return "~".concat(literal);
    }
	
	public static boolean ListaSadrziString(List<String> lista, String string) {
		for(String element : lista) {
			if(element.equals(string)) {
				return true;
			}
		}
		
		return false;
	}

	public static Pair<Integer, Integer> ListaSadrziKlauzulu (List<List<String>> klauzulePoLiteralima, List<String> klauzulaPoLiteralima) {
        for(int i = 0; i < klauzulePoLiteralima.size(); i++) {
        	List<String> klauzula = klauzulePoLiteralima.get(i);
        	Collections.sort(klauzula);
        	Collections.sort(klauzulaPoLiteralima);
            if (klauzula.equals(klauzulaPoLiteralima))
            {
                return new Pair<Integer, Integer>(1, i);
            }
        }

        return new Pair<Integer, Integer>(0, -1);
    }
	
	public static boolean ListaSadrziPar(List<Pair<Integer, Integer>> lista, Pair<Integer, Integer> par) {
		for(Pair<Integer, Integer> element : lista) {
			if(element.getKey() == par.getKey() && element.getValue() == par.getValue()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static List<Integer> Distinct(List<Integer> listaInt) {
	    return listaInt.stream().distinct().collect(Collectors.toList());
	}

    public static Pair<Integer, Integer> PrimjenaPravila(List<List<String>> sveKlauzulePoLiteralima, List<Pair<Integer, Integer>> upareneKlauzule, int brPocetnihKlauzula)
    {
    	List<Pair<Pair<Integer, Integer>, Integer>> prioritet = new ArrayList<>();
    	List<Pair<Pair<Integer, Integer>, Integer>> sortiraniPrioritet = new ArrayList<>();
        for (int i = brPocetnihKlauzula; i < sveKlauzulePoLiteralima.size(); i++) {
            List<String> klauzulaIzSkupaPotpore = sveKlauzulePoLiteralima.get(i);
            
            for (int j = 0; j < sveKlauzulePoLiteralima.size(); j++) {
            	List<String> klauzula2 = sveKlauzulePoLiteralima.get(j);

                boolean primjenjivoPravilo = false;
                for (int k = 0; k < klauzulaIzSkupaPotpore.size(); k++) {
                	String literal1 = klauzulaIzSkupaPotpore.get(k);
                	
                	for (int l = 0; l < klauzula2.size(); l++) {
                		String literal2 = klauzula2.get(l);
                		
                        if (literal1.equals(SuprotanLiteral(literal2))) {
                        	
                            primjenjivoPravilo = true;

                            List<String> literaliStarihKlauzula = new ArrayList<>();
                            literaliStarihKlauzula.addAll(klauzulaIzSkupaPotpore);
                            literaliStarihKlauzula.addAll(klauzula2);
                            
                            List<String> novaKlauzulaPoLiteralima = new ArrayList<>();
                            for (String literal : literaliStarihKlauzula) {
                                if (ListaSadrziString(literaliStarihKlauzula, SuprotanLiteral(literal)) == false) {
                                	novaKlauzulaPoLiteralima.add(literal);
                                }
                            }

                            for (List<String> klauzulaPoLiteralima : sveKlauzulePoLiteralima) {
                            	Collections.sort(klauzulaPoLiteralima);
                            	Collections.sort(novaKlauzulaPoLiteralima);
                                if (klauzulaPoLiteralima.equals(novaKlauzulaPoLiteralima)) {
                                    primjenjivoPravilo = false;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                	
                    Pair<Integer, Integer> parKlauzula = new Pair<Integer, Integer>(j, i);
                    if (primjenjivoPravilo && ListaSadrziPar(upareneKlauzule, parKlauzula) == false) {
                    	prioritet.add(new Pair<Pair<Integer,Integer>, Integer>(parKlauzula, klauzulaIzSkupaPotpore.size() + klauzula2.size()));
                        break;
                    }
                }
            }

        }

        if (prioritet.size() != 0)
        {        	
        	Collections.sort(prioritet, new Comparator<Pair<Pair<Integer, Integer>, Integer>>() { 
                public int compare(Pair<Pair<Integer, Integer>, Integer> o1, Pair<Pair<Integer, Integer>, Integer> o2) { 
                    return (o1.getValue()).compareTo(o2.getValue()); 
                } 
            });
        	
            for (Pair<Pair<Integer, Integer>, Integer> listItem : prioritet) { 
            	sortiraniPrioritet.add(listItem); 
            }
            
            return sortiraniPrioritet.get(0).getKey();
        }

        return null;
    }
}
