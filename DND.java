import java.util.Random;

// === SUPER CLASS ===
class Player {
    // Encapsulation: Semua atribut dibuat private
    private String name;
    private String race;
    private int baseHealth;
    private int baseAttack;
    private int incrementHealth;
    private int incrementAttack;
    private int level;
    private int exp;
    private int expToNextLevel;
    private int totalDamage;
    private boolean isAlive;

    // Object Member
    private Armor armor;
    private Weapon weapon;

    public Player(String name, String race) {
        this.name = name;
        this.race = race;
        this.baseHealth = 100;
        this.baseAttack = 10;
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 50; // Butuh 50 EXP untuk naik ke level 2
        this.incrementHealth = 20;
        this.incrementAttack = 5;
        this.isAlive = true;
    }

    // Encapsulation: Getter
    public String getName() { return this.name; }
    public String getRace() { return this.race; }
    public int getLevel() { return this.level; }
    public boolean getIsAlive() { return this.isAlive; }
    public Weapon getWeapon() { return this.weapon; }

    public int getHealth() {
        return this.maxHealth() - this.totalDamage;
    }

    public int maxHealth() {
        int armorHealth = (this.armor != null) ? this.armor.getAddHealth() : 0;
        return this.baseHealth + (this.level * this.incrementHealth) + armorHealth;
    }

    // Attack power dinamis berdasarkan level dan senjata
    public int getAttackPower() {
        int weaponAttack = (this.weapon != null) ? this.weapon.getAttack() : 0;
        return this.baseAttack + (this.level * this.incrementAttack) + weaponAttack;
    }

    // Method Roll Dadu D20
    protected int rollD20() {
        Random rand = new Random();
        return rand.nextInt(20) + 1;
    }

    public void display() {
        System.out.println("--- [ STATISTIK " + this.name + " (" + this.race + ") ] ---");
        System.out.println("Level\t\t: " + this.level + " (EXP: " + this.exp + "/" + this.expToNextLevel + ")");
        System.out.println("Health\t\t: " + this.getHealth() + "/" + this.maxHealth());
        System.out.println("Attack\t\t: " + this.getAttackPower());
        System.out.println("Status\t\t: " + (this.isAlive ? "Alive" : "Dead") + "\n");
    }

    // Method default, akan di-override
    public void attack(Player opponent) {
        System.out.println(this.name + " melakukan serangan biasa.");
    }

    public void defence(int damage) {
        int defencePower = (this.armor != null) ? this.armor.getDefencePower() : 0;
        int deltaDamage;

        if (damage > defencePower) {
            deltaDamage = damage - defencePower;
        } else {
            deltaDamage = 0;
        }

        this.totalDamage += deltaDamage;
        System.out.println(this.name + " menahan dengan DEF: " + defencePower + ". Menerima " + deltaDamage + " damage!");

        if (this.getHealth() <= 0) {
            this.isAlive = false;
            this.totalDamage = this.maxHealth();
            System.out.println(this.name + " telah GUGUR!");
        }
    }

    // Fitur Baru: Sistem EXP dan Leveling
    public void gainExp(int gainedExp) {
        if (!this.isAlive) return;
        this.exp += gainedExp;
        System.out.println(this.name + " mendapatkan " + gainedExp + " EXP!");
        
        if (this.exp >= this.expToNextLevel) {
            this.levelUp();
        }
    }

    // Encapsulation: Private Method
    private void levelUp() {
        this.level++;
        this.exp -= this.expToNextLevel; // Sisa EXP dibawa ke level berikutnya
        this.expToNextLevel += 50; // Target EXP naik
        this.totalDamage = 0; // Full Heal saat naik level
        System.out.println("LEVEL UP! " + this.name + " naik ke Level " + this.level + "! (HP Dipulihkan Sepenuhnya)\n");
    }

    // Encapsulation: Setter
    public void setArmor(Armor armor) { this.armor = armor; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
}

// === INHERITANCE: Class Fighter ===
class Fighter extends Player {
    private String combatStyle;

    public Fighter(String name, String race, String combatStyle) {
        super(name, race);
        this.combatStyle = combatStyle;
    }

    @Override
    public void attack(Player opponent) {
        if (!this.getIsAlive()) return;

        System.out.println("\nGiliran: " + this.getName() + " (Fighter - " + this.combatStyle + ")");
        int diceRoll = rollD20();
        System.out.println("Hasil lempar dadu D20: " + diceRoll);

        if (diceRoll == 1) {
            System.out.println("CRITICAL FAIL! Serangan " + this.getName() + " meleset total!");
        } else {
            // Kalkulasi damage: Base Attack Power + Bonus Dadu
            int totalDamage = this.getAttackPower() + (diceRoll * 2);
            System.out.println(this.getName() + " menebas " + opponent.getName() + " dengan " + this.getWeapon().getName() + "!");
            opponent.defence(totalDamage);
            
            // Mendapatkan EXP setelah berhasil menyerang
            this.gainExp(30); 
        }
    }
}

// === INHERITANCE: Class Wizard ===
class Wizard extends Player {
    public Wizard(String name, String race) {
        super(name, race);
    }

    @Override
    public void attack(Player opponent) {
        if (!this.getIsAlive()) return;

        System.out.println("\nGiliran: " + this.getName() + " (Wizard)");
        int diceRoll = rollD20();
        System.out.println("Hasil lempar dadu D20: " + diceRoll);

        if (diceRoll > 5) {
            int spellDamage = this.getAttackPower() + 20; // Wizard punya bonus magic damage
            System.out.println(this.getName() + " merapalkan 'Fireball' ke arah " + opponent.getName() + "!");
            opponent.defence(spellDamage);
            this.gainExp(40); // Wizard dapat EXP lebih banyak dari spell
        } else {
             System.out.println("Spell gagal dirapalkan karena hasil dadu terlalu rendah!");
             this.gainExp(10); // Tetap dapat EXP sedikit
        }
    }
}

// === EQUIPMENTS ===
class Weapon {
    private String name;
    private int attack;

    public Weapon(String name, int attack) {
        this.name = name;
        this.attack = attack;
    }
    public String getName() { return this.name; }
    public int getAttack() { return this.attack; }
}

class Armor {
    private String name;
    private int strength;
    private int health;

    public Armor(String name, int strength, int health) {
        this.name = name;
        this.strength = strength;
        this.health = health;
    }
    public int getAddHealth() { return this.strength * 10 + this.health; }
    public int getDefencePower() { return this.strength * 2; }
}

// === MAIN PROGRAM ===
public class DND {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("DUNGEONS & DRAGONS: LEVELING EDITION");
        System.out.println("=========================================\n");

        // Membuat Party Members
        Fighter aurelius = new Fighter("Aurelius", "Semi Dragon", "Brawler");
        Fighter andrea = new Fighter("Andrea", "Human", "Swordsman");
        Wizard kaelen = new Wizard("Kaelen", "Elf");

        // Membuat Equipments
        Weapon cakar = new Weapon("Dragon Claw", 15);
        Weapon pedang = new Weapon("Iron Sword", 20);
        Weapon tongkat = new Weapon("Wooden Staff", 10);

        Armor sisik = new Armor("Dragon Scale", 10, 50);
        Armor bajuZirah = new Armor("Light Armor", 15, 80);
        Armor jubah = new Armor("Cloth Robe", 5, 30);

        // Equip Item
        aurelius.setWeapon(cakar);
        aurelius.setArmor(sisik);
        
        andrea.setWeapon(pedang);
        andrea.setArmor(bajuZirah);

        kaelen.setWeapon(tongkat);
        kaelen.setArmor(jubah);

        // Membuat Musuh (Boss)
        Fighter bossMonster = new Fighter("Goblin King", "Goblin", "Giant Club Smasher");
        bossMonster.setWeapon(new Weapon("Giant Spiked Club", 25));
        bossMonster.setArmor(new Armor("Hardened Skin", 20, 200));

        // Menampilkan Info Awal
        aurelius.display();
        bossMonster.display();

        // Simulasi Pertempuran dan Leveling
        System.out.println(">>> COMBAT INITIATED: AURELIUS VS GOBLIN KING <<<");
        
        // Aurelius menyerang berulang kali untuk membuktikan sistem EXP dan Level Up
        aurelius.attack(bossMonster); // Dapat 30 EXP
        aurelius.attack(bossMonster); // Dapat 30 EXP -> Total 60 EXP (Memicu Level Up!)
        
        System.out.println("\n>>> STATISTIK AURELIUS SETELAH LEVEL UP <<<");
        aurelius.display(); // Level akan naik, HP bertambah, Attack bertambah
    }
}