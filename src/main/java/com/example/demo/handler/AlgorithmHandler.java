package com.example.demo.handler;
import com.example.demo.dataAccessObject.DistrictRepo;
import com.example.demo.dataAccessObject.DistrictingRepo;
import com.example.demo.dataAccessObject.JobRepo;
import com.example.demo.dataAccessObject.PrecinctRepo;
import com.example.demo.enumerate.Minority;
import com.example.demo.enumerate.State;
import com.example.demo.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AlgorithmHandler {
    private Job job;
    private Map<Integer, Precinct> precinctDict = new HashMap<>();
    private Result result;

    @Autowired
    JobRepo jobRepo;

//    @Autowired
//    PrecinctRepo precinctRepo;

    @Autowired
    DistrictRepo districtRepo;

    @Autowired
    DistrictingRepo districtingRepo;

    // 通知状态 -> 转送文件 -> 处理结果
    public void processResult(int jobId) throws Exception {
        this.job = jobRepo.getOne(jobId);
        String state = job.getState().name();

        getPrecinctsFromJson(state);
        System.out.println("Processed state json to read precincts data");

        processResultJson();
        System.out.println("Processed result json");

        computeNumberOfCountiesForEachDistrict();
        System.out.println("Computed number of counties for each district");

        computeMinoritiesVapForEachDistrict();
        System.out.println("Computed minorities vap for each district");

        sortDistrictsForEachDistricting();
        System.out.println("Sorted districts in each districting according to their minorities vap percentage");

        generateSummary();
        System.out.println("Generated box and whisker data");

        System.out.println("Start generating average, extreme, and random plans");
        determineAverage();
        System.out.println("Generated average plan");
        determineExtreme();
        System.out.println("Generated extreme plan");
        determineRandom();
        System.out.println("Generated random plan");

//         把districting plan转换为GeoJSON
        Districting average = job.getAverage();
        convertDistrictingToJson(average, state, "average");

        Districting extreme = job.getExtreme();
        convertDistrictingToJson(extreme, state, "extreme");

        Districting random = job.getRandom();
        convertDistrictingToJson(random, state, "random");

        System.out.println("Start generating geojson files for average, extreme, and random plans");
        average.setGeojsonFilePath("src/main/resources/results/"+job.getJobId()+"_average_geo.json");
        extreme.setGeojsonFilePath("src/main/resources/results/"+job.getJobId()+"_extreme_geo.json");
        random.setGeojsonFilePath("src/main/resources/results/"+job.getJobId()+"_random_geo.json");
        System.out.println("Generated geojson files for average, extreme, and random plans");

        System.out.println("Generating summary json");
        generateSummaryJson();
        job.setSummaryFilePath("src/main/resources/results/"+job.getJobId()+"_summary.json");
        System.out.println("Generated summary json");

        job.setStatus("Completed");
        System.out.println("Persisting");
        jobRepo.save(job);
        System.out.println("Persisted");
    }

    // 从json读取precinct信息到内存里，方便之后快速查询precinct信息
    // 比从数据库中读取precinct信息快
    public void getPrecinctsFromJson(String stateName) throws Exception{
        System.out.println("Making precinct objects from json!");
        String state = stateName;
//        if(stateName.equals("GA")) {
//            state = State.GEORGIA.name().toLowerCase();
//        } else if(stateName.equals("LA")) {
//            state = State.LOUISIANA.name().toLowerCase();
//        } else if(stateName.equals("MI")) {
//            state = State.MISSISSIPPI.name().toLowerCase();
//        } else {
//            return;
//        }
        System.out.println("State: "+state);
        String filePath = "src/main/resources/static/"+state+".json";
        File file = new File(filePath);
        String content = FileUtils.readFileToString(file);
        //对基本类型的解析
        JSONObject obj = new JSONObject(content);
        JSONArray precinctArray = obj.getJSONArray("features");
        System.out.println("Length: "+precinctArray.length());
        for(int i=0; i<precinctArray.length(); i++) {
            JSONObject properties = ((JSONObject) precinctArray.get(i)).getJSONObject("properties");
            int precinctId = properties.getInt("ID");

            String countyName;
            try {
                countyName = properties.getString("CTYNAME");
            } catch(Exception e) {
                countyName = String.valueOf(properties.getInt("CTYNAME"));
            }

            int totalPopulation = properties.getInt("TOTPOP");
            int vap = properties.getInt("VAP");
            int hvap = properties.getInt("HVAP");
            int wvap = properties.getInt("WVAP");
            int bvap = properties.getInt("BVAP");
            int aminvap = properties.getInt("AMINVAP"); // American Indians and Alaska Natives
            int asianvap = properties.getInt("ASIANVAP");
            int nhpivap = properties.getInt("NHPIVAP"); // Native Hawaiians and other Pacific Islanders

            Demographics demographics = new Demographics();
            demographics.setPopulation(totalPopulation);
            demographics.setVotingAgePopulation(vap);
            demographics.setHispanicVap(hvap);
            demographics.setWhiteVap(wvap);
            demographics.setBlackVap(bvap);
            demographics.setAMINVap(aminvap);
            demographics.setAsianVap(asianvap);
            demographics.setNHPIVap(nhpivap);

            Precinct precinct = new Precinct();
            precinct.setPrecinctId(precinctId);
            precinct.setState(state);
            precinct.setCountyName(countyName);
            precinct.setDemographics(demographics);

            this.precinctDict.put(precinctId, precinct);
        }
    }

    public void processResultJson() throws IOException {
        System.out.println("Processing result json");
        this.result = new Result();

        File file = new File("src/main/resources/results/"+job.getJobId()+"_0.json");
        String content = FileUtils.readFileToString(file);
        JSONArray plans = new JSONArray(content);

        // for each plan in result
        for(int i=0; i<plans.length(); i++) {
            Districting districting = new Districting();
            JSONArray districts = (JSONArray) plans.get(i);
            // for each district in each plan:
            for(int j=0; j<districts.length(); j++) {
                District district = new District();
                JSONArray precincts = (JSONArray) districts.get(j);
                // for each precinct in each district:
                for(int k=0; k<precincts.length(); k++) {
                    int precinctId = (int) precincts.get(k);
//                    Precinct p = precinctRepo.getOne(precinctId);
                    Precinct p = this.precinctDict.get(precinctId);
                    district.getPrecincts().add(p);
                }
                districting.getDistricts().add(district);
            }
            this.result.getDistrictings().add(districting);
        }

        file = new File("src/main/resources/results/"+job.getJobId()+"_1.json");
        content = FileUtils.readFileToString(file);
        plans = new JSONArray(content);

        // for each plan in result
        for(int i=0; i<plans.length(); i++) {
            Districting districting = new Districting();
            JSONArray districts = (JSONArray) plans.get(i);
            // for each district in each plan:
            for(int j=0; j<districts.length(); j++) {
                District district = new District();
                JSONArray precincts = (JSONArray) districts.get(j);
                // for each precinct in each district:
                for(int k=0; k<precincts.length(); k++) {
                    int precinctId = (int) precincts.get(k);
//                    Precinct p = precinctRepo.getOne(precinctId);
                    Precinct p = this.precinctDict.get(precinctId);
                    if(p==null) {
                        System.out.println("Precinct "+precinctId+" not found");
                    }
                    district.getPrecincts().add(p);
                }
                districting.getDistricts().add(district);
            }
            this.result.getDistrictings().add(districting);
        }
    }

    public void computeNumberOfCountiesForEachDistrict() {
        List<Districting> districtings = this.result.getDistrictings();

        for(Districting districting : districtings) {
            for(District district : districting.getDistricts()) {
                Set<String> countyNames = new HashSet<>();
                for (Precinct p : district.getPrecincts()) {
                    String countyName = p.getCountyName();
                    if (!countyNames.contains(countyName)) {
                        countyNames.add(countyName);
                    }
                }
                district.setNumberOfCounties(countyNames.size());
            }
        }
    }

    public void computeMinoritiesVapForEachDistrict() {
        Set<Minority> minorities = job.getMinorities();
        List<Districting> districtings = this.result.getDistrictings();
        for(Districting districting : districtings) {
            for(District district : districting.getDistricts()) {
                Demographics demographics = new Demographics();
                int minoritiesVap = 0;
                int vap = 0;
                int totpop = 0;
                int asianVap = 0;
                int blackVap = 0;
                int whiteVap = 0;
                int hispanicVap = 0;
                int aminvap = 0;
                int nhpivap = 0;

                for (Precinct p : district.getPrecincts()) {
                    for(Minority m : minorities) {
                        if(m==Minority.ASIAN) {
                            minoritiesVap += p.getDemographics().getAsianVap();
                        } else if(m==Minority.BLACK) {
                            minoritiesVap += p.getDemographics().getBlackVap();
                        } else if(m==Minority.WHITE) {
                            minoritiesVap += p.getDemographics().getWhiteVap();
                        } else if(m==Minority.HISPANIC) {
                            minoritiesVap += p.getDemographics().getHispanicVap();
                        } else if(m==Minority.AMIN) {
                            minoritiesVap += p.getDemographics().getAMINVap();
                        } else if(m==Minority.NHPI) {
                            minoritiesVap += p.getDemographics().getNHPIVap();
                        }
                    }

                    vap += p.getDemographics().getVotingAgePopulation();
                    totpop += p.getDemographics().getPopulation();
                    asianVap += p.getDemographics().getAsianVap();
                    blackVap += p.getDemographics().getBlackVap();
                    whiteVap += p.getDemographics().getWhiteVap();
                    hispanicVap += p.getDemographics().getHispanicVap();
                    aminvap += p.getDemographics().getAMINVap();
                    nhpivap += p.getDemographics().getNHPIVap();
                }
                demographics.setMinoritiesVap(minoritiesVap);
                demographics.setVotingAgePopulation(vap);
                demographics.setPopulation(totpop);
                demographics.setAsianVap(asianVap);
                demographics.setBlackVap(blackVap);
                demographics.setWhiteVap(whiteVap);
                demographics.setHispanicVap(hispanicVap);
                demographics.setAMINVap(aminvap);
                demographics.setNHPIVap(nhpivap);
                double minoritiesVapPercentage = minoritiesVap / new Double(vap);
//                System.out.println(minoritiesVapPercentage);
                minoritiesVapPercentage = ((double) Math.round(minoritiesVapPercentage * 1000)) / 1000;
//                System.out.println("Rounded: "+minoritiesVapPercentage);
                demographics.setMinoritiesVapPercentage(minoritiesVapPercentage);
                district.setDemographics(demographics);
            }
        }
    }

    public void generateSummaryJson() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String state = job.getState().name();
        String stateId;
        if(state.equals(State.GEORGIA.name())) {
            stateId = "GA";
        } else if(state.equals(State.MISSISSIPPI.name())) {
            stateId = "MI";
        } else {
            stateId = "LA";
        }

        String filePath = "src/main/resources/static/"+state+".json";
        File file = new File(filePath);
        String content = FileUtils.readFileToString(file);
        JSONObject precinctsGeojson = new JSONObject(content);

        // districtings
        JSONArray districtings = new JSONArray();

        // average
        JSONObject average = new JSONObject();
        filePath = "src/main/resources/results/"+job.getJobId()+"_average_geo.json";
        file = new File(filePath);
        content = FileUtils.readFileToString(file);
        JSONObject geojson = new JSONObject(content);
        average.put("congressionalDistrictsGeoJSON", geojson);
        average.put("districtingId", job.getAverage().getDistrictingId());

        JSONObject constraint = new JSONObject();
        constraint.put("compactnessLimit", job.getCompactnessGoal());
        constraint.put("populationDifference", job.getPopulationDifference());
        JSONArray minorities = new JSONArray(job.getMinorities());
        constraint.put("minorityGroups", minorities);
        average.put("constraint", constraint);
        districtings.put(average);

        // extreme
        JSONObject extreme = new JSONObject();
        filePath = "src/main/resources/results/"+job.getJobId()+"_extreme_geo.json";
        file = new File(filePath);
        content = FileUtils.readFileToString(file);
        geojson = new JSONObject(content);
        extreme.put("congressionalDistrictsGeoJSON", geojson);
        extreme.put("districtingId", job.getExtreme().getDistrictingId());
        extreme.put("constraint", constraint);
        districtings.put(extreme);

        // random
        JSONObject random = new JSONObject();
        filePath = "src/main/resources/results/"+job.getJobId()+"_random_geo.json";
        file = new File(filePath);
        content = FileUtils.readFileToString(file);
        geojson = new JSONObject(content);
        random.put("congressionalDistrictsGeoJSON", geojson);
        random.put("districtingId", job.getRandom().getDistrictingId());
        random.put("constraint", constraint);
        districtings.put(random);

        JSONObject obj = new JSONObject();
        obj.put("stateName", job.getState().name());
        obj.put("stateId", stateId);
        obj.put("precinctsGeojson", precinctsGeojson);
        obj.put("averageDistricting", job.getAverage().getDistrictingId());
        obj.put("extremeDistricting", job.getExtreme().getDistrictingId());
        obj.put("randomDistricting", job.getRandom().getDistrictingId());
        obj.put("districtings", districtings);

        //Write JSON file
        try (FileWriter fw = new FileWriter("src/main/resources/results/"+job.getJobId()+"_summary.json")) {
            fw.write(obj.toString());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void removePrecinctIds(JSONArray districts) {
//        for(int i=0; i<districts.length(); i++) {
//            JSONObject obj = (JSONObject) districts.get(i);
//            obj.remove("precinctIds");
//        }
//    }

    public void sortDistrictsForEachDistricting() {
        List<Districting> districtings = this.result.getDistrictings();
        for(Districting districting : districtings) {
            List<District> districts = districting.getDistricts();
            Collections.sort(districts, new Comparator<District>() {
                @Override
                public int compare(District d1, District d2) {
                    double dif = d1.getDemographics().getMinoritiesVapPercentage() - d2.getDemographics().getMinoritiesVapPercentage();
                    if(dif<0) {
                        return -1;
                    } else if(dif>0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }

    public void generateSummary() {
        // generate summary
        List<Districting> districtings = this.result.getDistrictings();
        List<Box> summary = new ArrayList<>();

        for(int i=0; i<districtings.get(0).getDistricts().size(); i++) {
            Box box = new Box();

            List<Double> minoritiesVapPercentages = new ArrayList<>();
            for(Districting districting : districtings) {
                // find the ith district of a districting
                District district = districting.getDistricts().get(i);
                minoritiesVapPercentages.add(district.getDemographics().getMinoritiesVapPercentage());
            }

            // compute q1, median, q3, min, max of minoritiesVapPercentage for this box
            System.out.println("Minorities VAP% List for Box "+i+": "+minoritiesVapPercentages);
            double median = findMedian(minoritiesVapPercentages);
            System.out.println("Median: "+median);
            List<Double> list1 = minoritiesVapPercentages.stream().filter(m->m<median).collect(Collectors.toList());
//            System.out.println("List1: "+list1);
            double q1 = findMedian(list1);
            System.out.println("Q1: "+q1);
            List<Double> list2 = minoritiesVapPercentages.stream().filter(m->m>median).collect(Collectors.toList());
//            System.out.println("List2: "+list2);
            double q3 = findMedian(list2);
            System.out.println("Q3: "+q3);
            double min = minoritiesVapPercentages.get(0); // minoritiesVapPercentages has been sorted
            double max = minoritiesVapPercentages.get(minoritiesVapPercentages.size()-1);

            box.setQ1(q1);
            box.setQ3(q3);
            box.setMedian(median);
            box.setMin(min);
            box.setMax(max);
            summary.add(box);
        }

        // update job
        for(Box box : summary) {
            box.setJob(job);
        }
        job.setSummary(summary);
        jobRepo.save(job);
    }

    public double findMedian(List<Double> doubles) {
        double median = 0.0;
        Collections.sort(doubles);
        int middle = doubles.size()/2;
        if(doubles.size()%2==0) { // even
            median = (doubles.get(middle-1)+doubles.get(middle)) / 2;
        } else { // odd
            median = doubles.get(middle);
        }
        return median;
    }

    public void determineAverage() {
        // determine the average districting
//        Date date=java.util.Calendar.getInstance().getTime();
//        System.out.println(date);
        Districting average = null;
        double minAvgDistanceToMedian = Integer.MAX_VALUE;
        List<Box> summary = this.job.getSummary();
        for(Districting districting:result.getDistrictings()) {
            double avgDistanceToMedian = 0;
            double totalDistance = 0;
            for(int i=0; i<districting.getDistricts().size(); i++) {
                double minoritiesVapPercentage = districting.getDistricts().get(i).getDemographics().getMinoritiesVapPercentage();
                double median = summary.get(i).getMedian();
                totalDistance += Math.abs(minoritiesVapPercentage-median);
            }
            avgDistanceToMedian = totalDistance / summary.size();
            if(avgDistanceToMedian<minAvgDistanceToMedian) {
                minAvgDistanceToMedian = avgDistanceToMedian;
                average = districting;
            }
        }

        for(District d:average.getDistricts()) {
            d.setDistricting(average);
            for(Precinct p:d.getPrecincts()) {
                d.getPrecinctIds().add(p.getPrecinctId());
//                p.getDistricts().add(d);
            }
        }
        districtingRepo.save(average);
//        date=java.util.Calendar.getInstance().getTime();
//        System.out.println(date);
        job.setAverage(average);
//        jobRepo.save(job);
//        date=java.util.Calendar.getInstance().getTime();
//        System.out.println(date);
    }

    public void determineExtreme() {
        // determine the extreme districting
        Districting extreme = null;
        double maxAvgDistanceToMedian = 0;
        List<Box> summary = this.job.getSummary();
        for(Districting districting:result.getDistrictings()) {
            double avgDistanceToMedian = 0;
            double totalDistance = 0;
            for(int i=0; i<districting.getDistricts().size(); i++) {
                double minoritiesVapPercentage = districting.getDistricts().get(i).getDemographics().getMinoritiesVapPercentage();
                double median = summary.get(i).getMedian();
                totalDistance += Math.abs(minoritiesVapPercentage-median);
            }
            avgDistanceToMedian = totalDistance / summary.size();
            if(avgDistanceToMedian>maxAvgDistanceToMedian) {
                maxAvgDistanceToMedian = avgDistanceToMedian;
                extreme = districting;
            }
        }

        for(District d:extreme.getDistricts()) {
            d.setDistricting(extreme);
            for(Precinct p:d.getPrecincts()) {
//                p.getDistricts().add(d);
                d.getPrecinctIds().add(p.getPrecinctId());
            }
        }
        districtingRepo.save(extreme);
        job.setExtreme(extreme);
//        jobRepo.save(job);
    }

    public void determineRandom() {
        int n = this.result.getDistrictings().size();
        int randomNum = ThreadLocalRandom.current().nextInt(0, n);
        Districting random = this.result.getDistrictings().get(randomNum);

        for(District d:random.getDistricts()) {
            d.setDistricting(random);
            for(Precinct p:d.getPrecincts()) {
//                p.getDistricts().add(d);
                d.getPrecinctIds().add(p.getPrecinctId());
            }
        }
        districtingRepo.save(random);
        job.setRandom(random);
//        jobRepo.save(job);
    }

    public void convertDistrictingToJson(Districting d, String state, String type) throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ow.writeValue(new File("src/main/resources/results/"+job.getJobId()+"_"+type+".json"), d);
        String districtingJsonPath = "src/main/resources/results/"+job.getJobId()+"_"+type+".json";
        String stateJsonPath = "src/main/resources/static/"+state+".json";

        ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/algorithm/merge.py",
                districtingJsonPath,
                stateJsonPath,
                Integer.toString(job.getJobId()),
                type
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        printProcessOutput(process);
    }

    private void printProcessOutput(Process process) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
