
public class test {
	public static void main(String[] args) {
		
		
		
		String[] commande = new String[3];
		String argument = "";
		argument = argument+"site1:Site(site2, true)";
		argument =argument + ";";
		argument = argument+"site2:Site(site3, false)";
		argument =argument + ";";	
		argument = argument+"site3:Site(site1, false)";
		argument =argument + ";";
		
		commande[0]="-cp";
		commande[1]="jade.boot";
		commande[2]=argument;
		
		jade.Boot.main(commande);

}
}
