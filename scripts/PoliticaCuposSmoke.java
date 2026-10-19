import com.rutasur.service.PoliticaCupos;
public class PoliticaCuposSmoke {
 public static void main(String[] args) {
  assertTrue(PoliticaCupos.permiteBloqueo(5,1,4),"Permite aforo justo");
  assertTrue(!PoliticaCupos.permiteBloqueo(5,2,4),"Impide sobreventa");
  assertTrue(!PoliticaCupos.permiteBloqueo(5,0,0),"Impide cantidad cero");
  assertTrue(PoliticaCupos.disponibles(5,3)==2,"Calcula restantes");
  assertTrue(PoliticaCupos.disponibles(5,6)==0,"No expone negativos");
  System.out.println("OK: 5 verificaciones de política de cupos");
 }
 private static void assertTrue(boolean b,String msg){if(!b)throw new AssertionError(msg);}
}
