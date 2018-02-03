import java.util.*;

public class Entropy {

    private LinkedHashMap<String,LinkedHashMap<String,Double>> childEntropy;
    private  LinkedHashMap<String,Integer> totalclassificationCount;
    private LinkedHashMap<String,LinkedHashMap> classificationCountWithAttributea;
    private Double rootEntropy;
    static public LinkedList classtype;
    LinkedHashMap<String,Double> rootGain = new LinkedHashMap<>();
    LinkedHashMap<String,Double> gainRatio = new LinkedHashMap<>();
    LinkedHashMap<String,Double> splitEntropy = new LinkedHashMap<>();

    private static double Log2(double n) {
        if(n ==0)
            return 0;
        return Math.log(n) / Math.log(2);
    }

    public LinkedHashMap<String, LinkedHashMap<String, Double>> getchildEntropy() {
        return childEntropy;
    }

    public void calculateGain(){
        new Gain(rootEntropy,childEntropy,classificationCountWithAttributea);
    }

    public void topEntropy() {                            //entropy of an attribute
        double rootEntropy = 0;
        Integer total = 0;
        for (Integer b : totalclassificationCount.values()) {
            total = total + b;
        }

        for (String b : totalclassificationCount.keySet()) {
            int p = totalclassificationCount.get(b);
            double prob = (double) p / total;
            rootEntropy -= prob * Log2(prob);                                  //
        }
        this.rootEntropy=rootEntropy;
    }


    public Entropy(LinkedList allAttributeValues, Integer resultIndex){
        Disp.display("start of Entropy");

        LinkedHashMap<String,LinkedHashMap<String,Double>> fentropy = new LinkedHashMap<>();     //To store the entropy of all attributes
        LinkedList attribute = (LinkedList)allAttributeValues.get(0);
        allAttributeValues.remove(0);                                     // To remove heading

        ListIterator allAttributeValuesIterator = allAttributeValues.listIterator();
        LinkedHashMap<String,Integer> totalclassificationCount = new LinkedHashMap<>();
        Set<String> uniqueClassifications= new HashSet();                          //To find the unique classifications

        while(allAttributeValuesIterator.hasNext()){
            LinkedList temp = (LinkedList)allAttributeValuesIterator.next();
            //System.out.println(temp);
            uniqueClassifications.add(temp.get(resultIndex).toString());

            if(totalclassificationCount.containsKey(temp.get(resultIndex).toString()))
            {
                int count = 1+ totalclassificationCount.get(temp.get(resultIndex).toString());
                totalclassificationCount.put(temp.get(resultIndex).toString(),count);
            }
            else{
                totalclassificationCount.put(temp.get(resultIndex).toString(),1);
            }
        }
        this.totalclassificationCount = totalclassificationCount;
        LinkedList classificationType = new LinkedList(uniqueClassifications);                    //Unique attributes
        this.classtype = new LinkedList(uniqueClassifications);
        LinkedHashMap<String,LinkedHashMap> classificationCountWithAttributes = new LinkedHashMap<>();
        for(int k=0;k<resultIndex;k++) {                        //TO calculate Entropy for all attributes

            LinkedHashMap<String, LinkedHashMap> classificationCount = new LinkedHashMap<>();      //Classification count with respect to value's
            allAttributeValuesIterator = allAttributeValues.listIterator();                                             // Values list
            while (allAttributeValuesIterator.hasNext()) {
                LinkedList conditionData = (LinkedList) allAttributeValuesIterator.next();   //values(D6,Rain,Cool,Normal,Strong=No)
                ListIterator classificationTypeIterator = classificationType.listIterator();
                while (classificationTypeIterator.hasNext()) {
                    String classification = classificationTypeIterator.next().toString();
                    if (conditionData.get(resultIndex).equals(classification)) {            //to check which classification(yes or no) it belongs to
                        if (classificationCount.containsKey(conditionData.get(k))) {        //if found then increment the corresponding classification count
                            LinkedHashMap hms = classificationCount.get(conditionData.get(k));
                            int i = 1 + Integer.parseInt(hms.get(classification).toString());
                            hms.put(classification, i);
                        }
                        else {                                                            //if not found then add the classification
                            ListIterator t = classificationType.listIterator();
                            LinkedHashMap<String, Integer> temp = new LinkedHashMap();
                            while (t.hasNext()) {
                                temp.put(t.next().toString(), 0);
                            }
                            temp.put(classification, 1);
                            classificationCount.put(conditionData.get(k).toString(), temp); //Classification attribute, name and count
                        }
                    }
                }
            }
            //System.out.println(classificationCount);

            LinkedHashMap<String, Double> entropy = new LinkedHashMap<>();                              //entropy of an attribute
            for (String a : classificationCount.keySet()) {

                double entropyValue = 0;

                LinkedHashMap<String, Integer> t = classificationCount.get(a);
                Integer total = 0;
                for (Integer b : t.values()) {
                    total = total + b;
                }
                for (String b : t.keySet()) {
                    int p = t.get(b);
                    double prob = (double) p / total;
                    entropyValue -= prob * Log2(prob);                                  //
                }
                entropy.put(a, entropyValue);
            }
            fentropy.put(attribute.get(k).toString(),entropy);
            classificationCountWithAttributes.put(attribute.get(k).toString(),classificationCount);
        }
        //System.out.println("FENTROPY="+fentropy);
        this.classificationCountWithAttributea = classificationCountWithAttributes;
        this.childEntropy = fentropy;


        topEntropy();
        calculateGain();
        Disp.display("End of Entropy");

    }
}
