package nl.tno.idsa.framework.utils;

import nl.tno.idsa.framework.population.PopulationDataProvider;
import nl.tno.idsa.framework.semantics_base.JavaSubclassFinder;
import nl.tno.idsa.framework.world.WorldModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Find usable environment data in the project path. Folders are identified by the presence of a token property file
 * entitled "idsa_data_root.txt". This should contain a property 'worldModel' that tells us which WorldModel implementation
 * (class name) is needed to parse the data, a property 'populationDataProvider' that tells us which PopulationDataProvider
 * implementation (class name) is needed to construct the synthetic population, and finally a property 'description' that
 * should contain a concise description of the data so that the user can decide which data to open.
 */
// TODO For now, we traverse the directory tree upward, until we find a root from which data files are available. ...
// We then immediately stop and return those data files. I.e. no further looking or scanning.
public class DataSourceFinder {

    /**
     * List the available data sources.
     * @return The list.
     * @throws IOException In case the data folders cannot be read.
     */
    public static List<DataSource> listDataSources()
    throws IOException {

        // Start at the current location and bubble upward until data sources are found.
        File file = new File(".").getAbsoluteFile();
        ArrayList<File> files = new ArrayList<>();
        while (files.size() == 0) {
            files = DataSourceFinder.listDataSources(file);
            file = file.getParentFile();
            if (file == null) {
                break;
            }
        }

        // Parse the data source descriptor files.
        List<DataSource> dataSources = new ArrayList<>(files.size());
        if (files.size() > 0)
        {
            HashMap<String, Class<? extends WorldModel>> modelClassesByName = classesByName(WorldModel.class);
            HashMap<String, Class<? extends PopulationDataProvider>> popClassesByName = classesByName(PopulationDataProvider.class);
            for (File dataDescriptor: files) {
                try {
                    Properties p = new Properties();
                    p.load(new FileInputStream(dataDescriptor));
                    String path = dataDescriptor.getCanonicalPath();
                    path = path.substring(0, path.lastIndexOf(File.separator));
                    String model = p.getProperty("worldModel");
                    Class<? extends WorldModel> modelClass = modelClassesByName.get(model);
                    String pop = p.getProperty("populationDataProvider");
                    Class<? extends PopulationDataProvider> popClass = popClassesByName.get(pop);
                    String description = p.getProperty("description");
                    dataSources.add(new DataSource(path, description, modelClass.newInstance(), popClass.newInstance()));
                }
                catch (Exception e) {
                    // Ignore. This data source is not valid.
                }
            }
        }

        // And return.
        return dataSources;
    }

    @SuppressWarnings("unchecked")
    private static <T> HashMap<String, Class<? extends T>> classesByName(Class<? extends T> baseClass) {
        Set modelClasses = JavaSubclassFinder.listSubclasses(baseClass); // Unchecked. Stupid Java.
        HashMap<String, Class<? extends T>> classesByName = new HashMap<>();
        for (Object classO : modelClasses) {
            Class classC = (Class) classO;
            classesByName.put(classC.getSimpleName(), classC);
        }
        return classesByName;
    }

    private static ArrayList<File> listDataSources(File root) {
        ArrayList<File> results = new ArrayList<>();
        listDataSources(root, results);
        return results;
    }

    private static void listDataSources(File root, List<File> into)  {
        if (root == null) {
            return;
        }
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile() && file.getName().contains("idsa_data_root.txt")) {
                into.add(file);
            } else if (file.isDirectory()) {
                listDataSources(file, into);
            }
        }
    }

    /**
     * Data source struct.
     */
    public static class DataSource {
        private String path;
        private String description;
        private WorldModel worldModel;
        private PopulationDataProvider populationDataProvider;

        private DataSource(String path, String description, WorldModel worldModel, PopulationDataProvider populationDataProvider) {
            this.path = path;
            this.description = description;
            this.worldModel = worldModel;
            this.populationDataProvider = populationDataProvider;
        }

        /**
         * @return Where the data source is.
         */
        public String getPath() {
            return path;
        }

        /**
         * @return Description of the data source.
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return Model to use to parse data source files.
         */
        public WorldModel getWorldModel() {
            return worldModel;
        }

        /**
         * @return Population data provider to use for creating a statistically valid population.
         */
        public PopulationDataProvider getPopulationDataProvider() {
            return populationDataProvider;
        }

        @Override
        public String toString() {
            return description != null ? description : path;
        }
    }
}
