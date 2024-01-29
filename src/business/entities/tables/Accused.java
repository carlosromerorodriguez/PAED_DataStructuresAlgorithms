package business.entities.tables;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"name", "rabbits", "profession", "heretic"})
public class Accused {
    private String name;
    private int rabbits;
    private String profession;
    private boolean heretic;

    public Accused(String name, int rabbits, String profession) {
        this.name = name;
        this.rabbits = rabbits;
        this.profession = profession;
        this.heretic = hasSeenMoreThan1975Rabbits(profession);
    }

    private boolean hasSeenMoreThan1975Rabbits(String profession) {
        return ((rabbits > 1975) &&
                (!profession.equals("KING") && !profession.equals("QUEEN") && !profession.equals("CLERGYMAN")));
    }

    public void setHeretic(boolean b) {
        this.heretic = b;
    }
}
