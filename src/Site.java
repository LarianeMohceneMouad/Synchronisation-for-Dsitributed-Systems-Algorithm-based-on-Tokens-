import jade.core.AID;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;

public class Site extends Agent{
	String etat = "dehors";
	String SiteSuccesseur;
	String jeton;

	boolean jeton_acquise = false;
	public void setup() {
		System.out.println("Agent "+getLocalName());
		Object [] args = getArguments();
		if (args != null)
			SiteSuccesseur = args[0].toString();
			jeton = args[1].toString();


		if (jeton.equals("true")) {
			jeton_acquise = true ;
		}
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new liberer(), "dehors");
		fsm.registerState(new acquirir(), "demandeur");
		fsm.registerState(new enSC(), "dedans");
		
		fsm.registerTransition("demandeur","demandeur", 0);
		fsm.registerTransition("demandeur","dedans", 1);
		fsm.registerDefaultTransition("dedans", "dehors");
		fsm.registerDefaultTransition("dehors", "demandeur");

		ParallelBehaviour parallel = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		parallel.addSubBehaviour(fsm);
		parallel.addSubBehaviour(new consulterBoite());

		addBehaviour(parallel);
		
		
		System.out.println("Je suis "+getLocalName()+" et je suis "+etat+ " avec jeton acquis : "+jeton_acquise);

	}
	public class acquirir extends OneShotBehaviour{
		int valTransition;
		public void action() {
			if (etat.equals("dehors")) {
				System.out.println("Je suis "+getLocalName()+" et je suis "+etat+ " avec jeton acquis : "+jeton_acquise);
				etat = "demandeur";
				valTransition = 0;


			}
			if (jeton_acquise == true) {
				valTransition =1;
				etat = "dedans";
				System.out.println("Je suis "+getLocalName()+" et je suis "+etat+ " avec jeton acquis : "+jeton_acquise);
			}	
		}
		public int onEnd() {
			return valTransition ;
		}	
	}
	public class liberer extends OneShotBehaviour {
		public void action() {
			if(etat.equals("dedans")) {
				etat = "dehors";
				jeton_acquise = false ;
				System.out.println("Je suis "+getLocalName()+" et j'ai liberer la SC");
				ACLMessage msgEnvoi = new ACLMessage(ACLMessage.INFORM);
				msgEnvoi.addReceiver(new AID(SiteSuccesseur , AID.ISLOCALNAME));
				msgEnvoi.setContent("jeton");
				send(msgEnvoi);
				System.out.println("Je suis "+getLocalName()+"et j'ai envoyer le jeton a "+SiteSuccesseur);
			}
		}
		public int onEnd() {
			int valTransition = (int)Math.random()*2;
			return valTransition;
		}
	}
	public class enSC extends OneShotBehaviour {
		public void action() {
			for(int i=0;i<5;i++)
				System.out.println("Agent "+getLocalName()+" je suis en SC ");
			block((int)(Math.random() * 1000)); 
		}	
	}
	public class consulterBoite extends CyclicBehaviour{
		public void action() {
			ACLMessage msgRecu = receive();
			if (msgRecu != null) {
				String msgContenu = msgRecu.getContent();
				if(msgContenu.equals("jeton")) {
					if (etat.equals("dehors")) {
						ACLMessage msgEnvoi = new ACLMessage(ACLMessage.INFORM);
						msgEnvoi.addReceiver(new AID(SiteSuccesseur , AID.ISLOCALNAME));
						msgEnvoi.setContent("jeton");
						send(msgEnvoi);
						System.out.println("Je suis "+getLocalName()+"et j'ai envoyer le jeton a "+SiteSuccesseur);
					}else {
					jeton_acquise = true ;
					System.out.println("je suis  "+getLocalName()+" j'ai recu un "+msgContenu+" de la part de "+msgRecu.getSender().getLocalName());
					}

				}
			}
		}

	}
}
