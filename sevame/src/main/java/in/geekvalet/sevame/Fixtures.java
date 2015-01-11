package in.geekvalet.sevame;

import in.geekvalet.sevame.model.Customer;
import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.model.Location;
import in.geekvalet.sevame.model.ServiceProvider;

/**
 * Created by gautam on 13/12/14.
 */
public class Fixtures {

    public Job getJob1() {
        Job job = new Job();

        job.setDescription("Flush Repair");
        job.setDate("20-05-2014 5:30 PM");
        job.setAddress("186, 2nd C Main, Koramangala 8th Block, Near Corporation Bank");
        job.setCustomer(new Customer("Gautam BT", "9620917775"));
        job.setLocation(new Location(12.935340, 77.623930));

        return job;
    }

    public Job getJob2() {
        Job job = new Job();

        job.setDescription("Commode installation");
        job.setAddress("186, 2nd C Main, Koramangala 8th Block, Near Corporation Bank");
        job.setDate("20-05-2014 5:30 PM");
        job.setCustomer(new Customer("Gautam BT", "9620917775"));
        job.setLocation(new Location(12.935340, 77.623930));

        return job;
    }

    public Job getJob3() {
        Job job = new Job();

        job.setDescription("Kitchen sink repair");
        job.setDate("20-05-2014 5:30 PM");
        job.setAddress("186, 2nd C Main, Koramangala 8th Block, Near Corporation Bank");
        job.setCustomer(new Customer("Gautam BT", "9620917775"));
        job.setLocation(new Location(12.935340, 77.623930));

        return job;
    }

    public Job getJob4() {
        Job job = new Job();

        job.setDescription("Piping");
        job.setDate("20-05-2014 5:30 PM");
        job.setAddress("186, 2nd C Main, Koramangala 8th Block, Near Corporation Bank");
        job.setCustomer(new Customer("Gautam BT", "9620917775"));
        job.setLocation(new Location(12.935340, 77.623930));

        return job;
    }

    public Job getJob5() {
        Job job = new Job();
        job.setDescription("Tap Repair");
        job.setDate("20-05-2014 5:30 PM");
        job.setAddress("186, 2nd C Main, Koramangala 8th Block, Near Corporation Bank");
        job.setCustomer(new Customer("Gautam BT", "9620917775"));
        job.setLocation(new Location(12.935340, 77.623930));

        return job;
    }

    public ServiceProvider getServiceProvider() {
        return new ServiceProvider("1234", "9620917775", "Gautam BT");
    }
}
