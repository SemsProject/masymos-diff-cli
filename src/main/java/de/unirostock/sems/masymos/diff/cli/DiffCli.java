package de.unirostock.sems.masymos.diff.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unirostock.sems.masymos.configuration.Config;
import de.unirostock.sems.masymos.database.Manager;
import de.unirostock.sems.masymos.diff.DiffExecutor;

public class DiffCli {

	private static Logger log = LoggerFactory.getLogger(DiffCli.class);
	
	public static void main(String[] args) {

		boolean cleanDiffs = false;
		long limit = 0;

		log.info("Started up");

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dbPath")) {
				Config.instance().setDbPath(args[++i]);
			}
			else if(args[i].equals("-cleanDiffs")) {
				cleanDiffs = true;
			}
			else if(args[i].equals("-limit")) {
				limit = Long.parseLong( args[++i] );
			}
			else if(args[i].equals("-threads")) {
				DiffExecutor.setThreadPoolSize( Integer.parseInt(args[++i]) );
			}
			else if(args[i].equals("-queryLimit")) {
				DiffExecutor.setQueryLimit( Integer.parseInt(args[++i]) );
			}
		}

		// create neo4j database
		long start = System.currentTimeMillis();
		log.info("Getting manager...");
		Manager.instance();
		DiffExecutor executor = DiffExecutor.instance();
		log.info("Got manager in {} mseconds", (System.currentTimeMillis() - start));

		if( cleanDiffs ) {
			log.info("Removing existing diffs enabled");
			start = System.currentTimeMillis();
			executor.removeAllDiffs();
			log.info("Cleaned up in {} mseconds", (System.currentTimeMillis() - start));
		}

		log.info("start generating diffs");
		if( limit > 0 )
			log.info("Limit is set to {}", limit);
		start = System.currentTimeMillis();
		executor.execute(limit);
		log.info("done in " + (System.currentTimeMillis() - start)
				+ "ms");

		System.exit(0);
	}
}
