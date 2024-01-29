package business.entities.avl_trees;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(of = {"id", "nom", "weight", "kingdom"})
public class Citizen implements Comparable<Citizen> {
    private int id;
    @NonNull
    private String nom;
    private float weight;
    @NonNull
    private String kingdom;

    public Citizen(int id, @NonNull String nom, float weight, @NonNull String kingdom) {
        this.id = id;
        this.nom = nom;
        this.weight = weight;
        this.kingdom = kingdom;
    }

    @Override
    public int compareTo(Citizen citizen) {
        return Float.compare(this.weight, citizen.getWeight());
    }

    @Override
    public String toString() {
        if (this.kingdom.matches("(?i)^[aeiou]{1}.*")) {
            return this.nom+" ("+this.id+", Regne d'"+this.kingdom+"): "+this.weight+"kg";
        }
        return this.nom+" ("+this.id+", Regne de "+this.kingdom+"): "+this.weight+"kg";
    }
}