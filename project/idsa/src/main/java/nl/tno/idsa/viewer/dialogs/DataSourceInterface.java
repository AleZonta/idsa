package nl.tno.idsa.viewer.dialogs;

import nl.tno.idsa.framework.utils.DataSourceFinder;

/**
 * Created by alessandrozonta on 06/09/16.
 */
public interface DataSourceInterface {
    String CAPTION = "Pick data source";

    boolean isCancelled();

    boolean areDataSourcesPresent();

    DataSourceFinder.DataSource getSelectedDataSource();
}
