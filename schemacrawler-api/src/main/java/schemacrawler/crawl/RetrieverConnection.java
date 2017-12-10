/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.schemacrawler.DatabaseSpecificOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.Identifiers;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TableTypes;
import schemacrawler.utility.TypeMap;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A connection for the retriever. Wraps a live database connection.
 *
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(RetrieverConnection.class.getName());

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final DatabaseSpecificOptions databaseSpecificOptions;
  private final MetadataRetrievalStrategy tableRetrievalStrategy;
  private final MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private final MetadataRetrievalStrategy pkRetrievalStrategy;
  private final MetadataRetrievalStrategy indexRetrievalStrategy;
  private final MetadataRetrievalStrategy fkRetrievalStrategy;
  private final InformationSchemaViews informationSchemaViews;
  private final TableTypes tableTypes;
  private final JavaSqlTypes javaSqlTypes;

  RetrieverConnection(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    try
    {
      checkConnection(connection);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SQLException("Bad database connection", e);
    }
    this.connection = connection;
    metaData = connection.getMetaData();

    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    informationSchemaViews = databaseSpecificOverrideOptions
      .getInformationSchemaViews();

    databaseSpecificOptions = new DatabaseSpecificOptions(connection,
                                                          databaseSpecificOverrideOptions);
    LOGGER.log(Level.CONFIG, new StringFormat("%s", databaseSpecificOptions));

    tableRetrievalStrategy = databaseSpecificOverrideOptions
      .getTableRetrievalStrategy();
    tableColumnRetrievalStrategy = databaseSpecificOverrideOptions
      .getTableColumnRetrievalStrategy();
    pkRetrievalStrategy = databaseSpecificOverrideOptions
      .getPrimaryKeyRetrievalStrategy();
    indexRetrievalStrategy = databaseSpecificOverrideOptions
      .getIndexRetrievalStrategy();
    fkRetrievalStrategy = databaseSpecificOverrideOptions
      .getForeignKeyRetrievalStrategy();

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Supported table types are <%s>", tableTypes));

    javaSqlTypes = new JavaSqlTypes();
  }

  public MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return fkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getIndexRetrievalStrategy()
  {
    return indexRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getPrimaryKeyRetrievalStrategy()
  {
    return pkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return tableRetrievalStrategy;
  }

  Connection getConnection()
  {
    return connection;
  }

  Identifiers getIdentifiers()
  {
    return databaseSpecificOptions.getIdentifiers();
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   *
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  JavaSqlTypes getJavaSqlTypes()
  {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  TableTypes getTableTypes()
  {
    return tableTypes;
  }

  TypeMap getTypeMap()
  {
    return databaseSpecificOptions.getTypeMap();
  }

  boolean isSupportsCatalogs()
  {
    return databaseSpecificOptions.isSupportsCatalogs();
  }

  boolean isSupportsSchemas()
  {
    return databaseSpecificOptions.isSupportsSchemas();
  }

}
