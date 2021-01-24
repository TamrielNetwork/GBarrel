package me.newt.multiplier;

public enum MultiplierType {

    MINECRAFT("Minecraft"),
    JOBS("Jobs"),
    MCMMO("mcMMO");

    private final String capitalizedName;

    /**
     * Constructor.
     * @param capitalizedName Correctly cased name for this type.
     */
    MultiplierType(String capitalizedName) {
        this.capitalizedName = capitalizedName;
    }

    /**
     * Get the capitalized name of this type.
     * @return Correctly cased name.
     */
    public String getCapitalizedName() {
        return capitalizedName;
    }
}
