package business.entities.graphs;

public record InterestPoint(int id, String nom, String regne,  String clima) {}

//Nom: Nom del lloc d’interès. Pot contenir més d’una paraula. No es garanteix que sigui únic.
//Regne: Nom del regne al qual pertany. Pot contenir més d’una paraula. No es garanteix que sigui únic.
//Clima pot ser CONTINENTAL, TROPICAL O POLAR
