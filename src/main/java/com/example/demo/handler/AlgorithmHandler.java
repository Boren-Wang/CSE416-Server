package com.example.demo.handler;

import com.example.demo.dataAccessObject.JobRepo;
import com.example.demo.dataAccessObject.PrecinctRepo;
import com.example.demo.enumerate.Minority;
import com.example.demo.enumerate.State;
import com.example.demo.model.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    PrecinctRepo precinctRepo;

    // 通知状态 -> 转送文件 -> 处理结果
    public void processResult() throws Exception {
        this.job = jobRepo.getOne(415231);
        getPrecinctsFromJson("GA");
        System.out.println("Processed state json to read precincts data");

        processResultJson();
        System.out.println("Processed result json");
        System.out.println(this.result);

        computeNumberOfCountiesForEachDistrict();
        System.out.println("Computed number of counties for each district");
        System.out.println(this.result);

        computeMinoritiesVapForEachDistrict();
        System.out.println("Computed minorities vap for each district");
        System.out.println(this.result);

        sortDistrictsForEachDistricting();
        System.out.println("Sorted districts in each districting according to their minorities vap percentage");
        System.out.println(this.result);

        generateSummary();
        System.out.println("Generated box and whisker data");
        System.out.println(job);

        determineAverage();
        determineExtreme();
        determineRandom();
        System.out.println("Generated average, extreme, and random plans");
        System.out.println(job);
    }

    // 从json读取precinct信息到内存里，方便之后快速查询precinct信息
    // 比从数据库中读取precinct信息快
    public void getPrecinctsFromJson(String stateName) throws Exception{
        System.out.println("Making precinct objects from json!");
        String state;
        if(stateName.equals("GA")) {
            state = State.GEORGIA.name().toLowerCase();
        } else if(stateName.equals("LA")) {
            state = State.LOUISIANA.name().toLowerCase();
        } else if(stateName.equals("MI")) {
            state = State.MISSISSIPPI.name().toLowerCase();
        } else {
            return;
        }
        System.out.println("State: "+state);
        File file = new File("src/main/resources/static/"+state+".json");
        String content = FileUtils.readFileToString(file);
        //对基本类型的解析
        JSONObject obj = new JSONObject(content);
        JSONArray precinctArray = obj.getJSONArray("features");
        System.out.println("Length: "+precinctArray.length());
        for(int i=0; i<precinctArray.length(); i++) {
            JSONObject properties = ((JSONObject) precinctArray.get(i)).getJSONObject("properties");
            int precinctId = properties.getInt("ID");
            String countyName = properties.getString("CTYNAME");
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

        File file = new File("src/main/resources/results/415231.json");
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
        System.out.println(this.result);
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
                }
                demographics.setMinoritiesVap(minoritiesVap);
                demographics.setVotingAgePopulation(vap);
                double minoritiesVapPercentage = minoritiesVap / new Double(vap);
                demographics.setMinoritiesVapPercentage(minoritiesVapPercentage);
                district.setDemographics(demographics);
            }
        }
    }

    public String generateSummaryFile(Job job) {
        return "";
    }

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
            double median = findMedian(minoritiesVapPercentages);
//            System.out.println("Median: "+median);
//            System.out.println("VAP% List: "+minoritiesVapPercentages);
            List<Double> list1 = minoritiesVapPercentages.stream().filter(m->m<median).collect(Collectors.toList());
//            System.out.println("List1: "+list1);
            double q1 = findMedian(list1);
            List<Double> list2 = minoritiesVapPercentages.stream().filter(m->m>median).collect(Collectors.toList());
//            System.out.println("List2: "+list2);
            double q3 = findMedian(list2);
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
        System.out.println("Computing the average plan");
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
        job.setAverage(average);
        jobRepo.save(job);
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
        job.setExtreme(extreme);
        jobRepo.save(job);
    }

    public void determineRandom() {
        int n = this.result.getDistrictings().size();
        int randomNum = ThreadLocalRandom.current().nextInt(0, n);
        Districting random = this.result.getDistrictings().get(randomNum);
        job.setRandom(random);
        jobRepo.save(job);
    }
}
