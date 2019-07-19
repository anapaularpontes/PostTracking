package com.PostTracking.Controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.PostTracking.Boundaries.CustomerDAO;
import com.PostTracking.Boundaries.DistributionCenterDAO;
import com.PostTracking.Boundaries.JourneyDAO;
import com.PostTracking.Boundaries.PackageDAO;
import com.PostTracking.Boundaries.RouteDAO;
import com.PostTracking.Boundaries.VehicleDAO;
import com.PostTracking.Entities.Customer;
import com.PostTracking.Entities.DistributionCenter;
import com.PostTracking.Entities.Route;
import com.PostTracking.Entities.Vehicle;
import com.PostTracking.Entities.Package;
import com.PostTracking.Entities.Path;
import com.PostTracking.Entities.Journey;

/**
 * Controls the endpoints for the Packages
 * @author 300296145
 *
 */
@Controller
public class PackageController {
	
	@Autowired
	RouteDAO rDAO;
	@Autowired
	JourneyDAO jDAO;
	@Autowired
	DistributionCenterDAO dcDAO;
	@Autowired
	PackageDAO pDAO;
	@Autowired
	CustomerDAO cDAO;
	@Autowired
	VehicleDAO vDAO;
	
	/**
	 * Maps the /packages (Filters to Packages)
	 * @param model the model to be sent to the view
	 * @return the view of /packages
	 */
	@GetMapping("/packages")
	public String fiterPackages(Model model) {
		model.addAttribute("package", new Package());
		return "packages/packages";
	}

	/**
	 * Maps the /packages/search and Returns a JSON of packages based on a search query
	 * @param origin_id the id of the Distribution Center (Origin) 
	 * @param destination_id the id of the Distribution Center (Destination)
	 * @param customer_id the id of the Customer
	 * @return JSON of packages
	 */
	@PostMapping("/packages/search")
	@ResponseBody
	public Iterable<com.PostTracking.Entities.Package> showPackages(@RequestParam String origin_id,@RequestParam String destination_id,@RequestParam String customer_id) {
		Customer customer = null;
		DistributionCenter origin = null;
		DistributionCenter destination = null;
		
		if(!origin_id.equals("0")) {origin = dcDAO.findById(Integer.parseInt(origin_id)).get();}
		if(!destination_id.equals("0")) {destination = dcDAO.findById(Integer.parseInt(destination_id)).get();}
		if(!customer_id.equals("0")) { customer = cDAO.findById(Integer.parseInt(customer_id)).get();}
		return pDAO.findBy(origin, destination, customer);
	}

	/**
	 * Maps the /packages/add
	 * @param model Adds the Package object to fill the form
	 * @return the View to Add the package
	 */
	@GetMapping("/packages/add")
	public String insert(Model model) {
		model.addAttribute("pack", new Package());
		return "packages/add";
	}

	/**
	 * Handles the creation of new Package.
	 * @param pack the Entity to be saved
	 * @param redirAttrs holds the message to the user
	 * @return Redirects to the view of list packages
	 */
	@PostMapping("/packages")
	public String createPackage(@ModelAttribute Package pack, RedirectAttributes redirAttrs) {
		if(!pack.validateMe()) {
			redirAttrs.addFlashAttribute("message", "Something went wrong :( ");
			return "redirect:/packages";	
		}
		pack.setPosition(pack.getOrigin());
		pack = pDAO.save(pack);
		redirAttrs.addFlashAttribute("message", "The Package has been Added!");
		return "redirect:/packages";
	}
	//TODO update description
	/**
	 * 
	 * @param pack 
	 * @param redirAttrs
	 * @return
	 */
	@PutMapping("/packages")
	public String updatePackage(@ModelAttribute Package pack, RedirectAttributes redirAttrs) {
		if(!pack.validateMe()) {
			redirAttrs.addFlashAttribute("message", "Something went wrong :( ");
			return "redirect:/packages";	
		}
		
		//Solving the "CLEANER Journeys"
		pack.setJourneys(pDAO.findById(pack.getId()).get().getJourneys());
		pack = pDAO.save(pack);
		redirAttrs.addFlashAttribute("message", "The Package has been Updated!");
		return "redirect:/packages";
	}

	/**
	 * Changes the position od a package
	 * @param position the distribution center id
	 * @param id the package
	 * @return 1 for good, 0 when error
	 */
	@GetMapping("/packages/updateposition/{position}/{id}")
	@ResponseBody
	public int updatePosition(@PathVariable String position,@PathVariable String id) {
		try {
			Package p = pDAO.findById(Integer.parseInt(id)).get();
			p.setPosition(dcDAO.findById(Integer.parseInt(position)).get());
			pDAO.save(p);
			return 1;
		} catch(Exception e) {	}
		return 0;
	}
	
	/**
	 * Maps /packages/{id} 
	 * @param id the Id of the Package
	 * @return the Package Object
	 */
	@GetMapping("/packages/{id}")
	@ResponseBody
	public Package getPackage(@PathVariable String id) {
		try {
			return pDAO.findById(Integer.parseInt(id)).get();
		} catch(Exception ex) {
			return new Package();
		}
	}
	
	@GetMapping("/packages/test")
	@ResponseBody
	public List<Journey> getJourneysBody() {
		return jDAO.getWithCapacity(new Timestamp (System.currentTimeMillis()));
	}
	
	
	//TODO update description
	/**
	 * Seeks the path between two distribution centers to route a package
	 * @param origin The origin DC 
	 * @param destination the destination DC 
	 * @return The JSON with possible Paths to reach the destination
	 */
	@GetMapping("/packages/seekpath/{origin}/{destination}/{weight_s}/{volume_s}")
	@ResponseBody
	public ArrayList<Path> seekPath(@PathVariable String origin,@PathVariable String destination,
			@PathVariable String weight_s,@PathVariable String volume_s) {
		ArrayList<Path> paths = new ArrayList<Path>();
		List<Route> routes = new ArrayList<Route>();
		rDAO.findAll().iterator().forEachRemaining(routes::add);
		int origin_id = 0;
		int destination_id = 0;
		double weight = 0.0;
		double volume = 0.0;
		try {
			origin_id = Integer.parseInt(origin);
			destination_id = Integer.parseInt(destination);
			weight = Double.parseDouble(weight_s);
			volume = Double.parseDouble(volume_s);
		} catch(Exception ex) {
			System.out.println("Unable to parse Origin or Destination on PackageController@seekPath");
			return null;
		}
		
		// Initializing paths to the first iteration
		paths.add(new Path(origin_id));
			
		for(int x=0; x < paths.size(); ++x) {
			for(int i=0 ; i < routes.size(); ++i) {
				/* Three conditions:
					-> if the Journey starts where the path is at this moment
					-> if the Journey doesn't go to a already visited place	
					-> if the the current position not the destination 
				*/ 
				if(routes.get(i).getOrigin().getId() == paths.get(x).getPosition() && 
						!paths.get(x).checkAlreadyVisited(routes.get(i).getDestination().getId()) &&
						paths.get(x).getPosition() != destination_id ) {
					// create a new path using the current one (Hard Copy) and adding the journey
					Path p = new Path(paths.get(x));
					p.addStep(new Route(routes.get(i)));
					paths.add(p);
					System.out.println("adding from: "+paths.get(x).getPosition()+" to: "+p.getPosition()+
							" Path progress: "+x+"/"+paths.size());
				} 
			}
		}

		//Setting the minimal time for journeys
		final long now = System.currentTimeMillis();
		long minimal = now;
		// Get the list of journeys ahead
		//List<Journey> journeys = jDAO.fetchFrom(new Timestamp (now));
		List<Journey> journeys = jDAO.getWithCapacity(new Timestamp (now));
		
		//Walk through Journeys
		for(int x=0; x < paths.size(); ++x) {	
			// This boolean tests the feasibility of the path
			boolean pathFeasibility = true;
			// If path is good (Reaches the destination)
			if(paths.get(x).getPosition() == destination_id) {
				System.out.println("Working on: "+x+" Paths Size ->"+paths.size());
				// At this moment, this is actually a List of Routes (Child of Journey)
				ArrayList<Journey> routesOfPath = paths.get(x).getJourneys();
				System.out.println(paths.get(x));
				// Backing minimal to now
				minimal = now;
				for(int i=0; i < routesOfPath.size() ; ++i) {
					Journey j = routesOfPath.get(i);
					
					//Testing the overall Capacity of the Vehicle
					if(j.getVehicle().getMaxVolume() < volume && j.getVehicle().getMaxWeight() < weight) {
						pathFeasibility = false;
						break;
					}
					
					// This Do-While keeps checking about the capacity (Volume/Weight)
					do {
						// Get a new the Possible Journey
						j = j.getNextPossible(minimal);
						// Check if there is already a journey created (Persistent)
						j = j.checkExistingJourney(journeys);
					
						if(j.getId() == 0) {
							// J receives the managed Entity (With ID :))
							j = jDAO.save(j);
							journeys.add(j);
						}
						// Swaping the route for the Journey
						routesOfPath.set(i, j);
						minimal = j.getArrival().getTime();
						
					} while(j.checkCapacity(volume, weight)==false);

					
					
				}
			// If current position != destination, drop
			} else {
				pathFeasibility = false;
			}
			
			//Removing path when there is no feasibility
			if(pathFeasibility==false ) {
				paths.remove(x);
				--x; //Backing X after index changed
			}
		}
		
		return paths;
	}

	@GetMapping("/packages/reroute/{id}/{origin}/{destination}/{weight_s}/{volume_s}")
	public String reRoutePackage(@PathVariable String id, @PathVariable String origin,
		@PathVariable String destination,@PathVariable String weight_s,@PathVariable String volume_s, Model m) {
			Package p = new Package();
			int origin_id = 0;
			List<Path> paths = new ArrayList<Path>();
			try {
				p = pDAO.findById(Integer.parseInt(id)).get();
				origin_id = Integer.parseInt(origin);
			} catch(Exception e) {

			}
			//The current position is the called Origin ID
			if(p.getPosition().getId()==origin_id) {
				paths = seekPath(origin, destination, weight_s, volume_s);
			}
			m.addAttribute("paths", paths);
			m.addAttribute("pack", p);
			return "packages/reroute";
	}

	@PostMapping("/packages/reroute")
	@ResponseBody
	public Package executeReRoute(@ModelAttribute Package pack, RedirectAttributes redirAttrs) {
		//Setting Up the Comparator
		Comparator<Journey> compareByStart = (Journey j1, Journey j2) ->
                                    j1.getStart().compareTo( j2.getStart() );

		// Filling the object (just ID and Journeys come from View)
		List<Journey> newJourneys = new ArrayList<Journey>(pack.getJourneys());
		Collections.sort(newJourneys, compareByStart);

		//Getting other fields from the Database
		pack = pDAO.findById(pack.getId()).get();

		if(pack.validateMe()) {
			List<Journey> currentJourneys = new ArrayList<Journey>(pack.getJourneys());
			Collections.sort(currentJourneys, compareByStart);
			Set<Journey> newMergedJourneys = new HashSet<Journey>();
			
			for(int i=0; i < currentJourneys.size() ; ++i) {
				//Finding the Journeys already done (to keep)
				if(newJourneys.get(0).getOrigin().getId()==currentJourneys.get(i).getOrigin().getId()) {
					break;
				} else {
					newMergedJourneys.add(currentJourneys.get(i));
				}
			}
			newMergedJourneys.addAll(newJourneys);
			pack.setJourneys(newMergedJourneys);
			pDAO.save(pack);
			
		} else {
			redirAttrs.addFlashAttribute("message", "Something went wrong :( ");
			//return "redirect:/packages";	
			System.out.println("fudeu");
		}
		return pack;
	}

	
	/**
	 * Makes the packages list available to the views
	 * @return list of packages
	 */
	@ModelAttribute("packages")
	public Iterable<Package> getAll() {
		return pDAO.findAll();
	}
	
	/**
	 * Makes the Distribution Centers list available to the view Add package
	 * @return list of distribution centers
	 */
	@ModelAttribute("distributionCenters")
	public Iterable<DistributionCenter> getDistributionCentes() {
		return dcDAO.fetchDCs();
	}

	/**
	 * Makes the Customer list available to the view Add package
	 * @return list of Customers
	 */
	@ModelAttribute("customers")
	public Iterable<Customer> getCustomers() {
		return cDAO.findAll();
	}
	
	/**
	 * This ModeAttribute is necessary because without it, seekpach calls 1 select to each journeys on GetWithCapacity
	 * @return Iterable of Journeys
	 */
	@ModelAttribute("journeys")
	public Iterable<Journey> getJourneys() {
		return jDAO.findAll();
	}
	
	/**
	 * This ModeAttribute is necessary because without it, seekpach calls 1 select to each vehicle
	 * @return The list of all vehicles available on the database.
	 */
	@ModelAttribute("vehicles")
	public Iterable<Vehicle> getVehicles() {
		return vDAO.findAll();
	}
}

