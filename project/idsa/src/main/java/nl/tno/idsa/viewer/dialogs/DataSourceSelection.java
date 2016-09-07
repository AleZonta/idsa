package nl.tno.idsa.viewer.dialogs;

import nl.tno.idsa.framework.utils.DataSourceFinder;

import java.util.List;

/**
 * Created by alessandrozonta on 06/09/16.
 */
public class DataSourceSelection implements DataSourceInterface {
    private boolean dataSourcesPresent;
    private boolean cancelled;
    private DataSourceFinder.DataSource selectedDataSource;

    //constructor for the version without dialog
    public DataSourceSelection(){
        List<DataSourceFinder.DataSource> dataSources;
        DataSourceFinder.DataSource defaultDataSource;
        try {
            dataSources = DataSourceFinder.listDataSources();
            defaultDataSource = dataSources.get(0);
            dataSourcesPresent = true;
        }
        catch(Exception e) {
            dataSourcesPresent = false;
            return;
        }
        cancelled = false;
        selectedDataSource = defaultDataSource;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean areDataSourcesPresent() {
        return dataSourcesPresent;
    }

    public DataSourceFinder.DataSource getSelectedDataSource() {
        return selectedDataSource;
    }
}
