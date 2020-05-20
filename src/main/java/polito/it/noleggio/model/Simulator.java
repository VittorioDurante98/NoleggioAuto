package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {
	
	// Coda degli eventi
	private PriorityQueue<Event> queue = new PriorityQueue<>();
	
	//parametri di simulazione
	private int VC = 10; //numero cars 
	private Duration T_I= Duration.of(10, ChronoUnit.MINUTES); // intervallo tra i clienti
	private final LocalTime oraApertura = LocalTime.of(8, 00);
	private final LocalTime oraChiusura = LocalTime.of(17, 00);
	
	//modello del mondo
	private int nAuto; // auto disp in deposito (tra 0 e VC)
	
	//valori da calcolare
	private int clienti;
	private int insoddisfatti;
	
	
	//Metodi per impostare i parametri
	public void setNumCars(int n) {
		this.VC= n;
	}

	public void setClientFrequency(Duration of) {
		this.T_I=of;
	}

	//metodi per restituire i risultati
	public int getClienti() {
		return clienti;
	}

	public int getInsoddisfatti() {
		return insoddisfatti;
	}
	
	//simulazione 
	public void run() {
		//preparazione iniziale (mondo + coda eventi)
		this.nAuto= this.VC;
		this.clienti = this.insoddisfatti = 0;
		
		this.queue.clear();
		LocalTime oraArrivoCliente = this.oraApertura;
		do {
			Event e = new Event(oraArrivoCliente, EventType.VnEW_CLIEnTE);
			this.queue.add(e);
		    oraArrivoCliente = oraArrivoCliente.plus(this.T_I);
		} while (oraArrivoCliente.isBefore(this.oraChiusura));
		
		//esecuzione del ciclo di simulazione 
		while (!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
	}
	
	private void processEvent(Event e) {
		switch (e.getType()) {
		case VnEW_CLIEnTE:
			if(this.nAuto>0) {
				//cliente viene servito, auto noleggiata
				//1. aggiorna modello del mondo
				this.nAuto--;
				//2. aggiorna i risultati
				this.clienti++;
				//3. genera nuovi clienti
				double num = Math.random();//(0,1)
				Duration travel;
				if (num<1.0/3.0) {
					travel = Duration.of(1, ChronoUnit.HOURS);
				} else if (num<2.0/3.0) {
					travel = Duration.of(2, ChronoUnit.HOURS);
				}else {
					travel = Duration.of(3, ChronoUnit.HOURS);
				}
				System.out.println(travel);
				Event nuovo = new Event(e.getTime().plus(travel), EventType.CAR_RETURVED);
				this.queue.add(nuovo);
				
			}else {
				// cliete insoddisfatto
				this.clienti++;
				this.insoddisfatti++;
			}
			break;

		case CAR_RETURVED:
			
			this.nAuto++;
			
			
			break;
		}
	}
}
