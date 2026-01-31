package service;

import api.LeonApi;
import api.LeonApiImpl;
import dto.Event;
import dto.League;
import dto.Region;
import dto.Sport;
import mapper.Mapper;
import mapper.MapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

import static api.LeonUrlsUtil.GET_SPORTS;


public class ExecutorImpl implements Executor {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorImpl.class);
    private final ExecutorService leaguePool = Executors.newFixedThreadPool(3);
    private final ExecutorService eventPool = Executors.newFixedThreadPool(3);
    private final LongAdder processedEvents = new LongAdder();

    private final Mapper mapper;
    private final LeonApi api;

    public ExecutorImpl() {
        this.api = new LeonApiImpl();
        this.mapper = new MapperImpl();
    }

    @Override
    public void execute() {
        List<CompletableFuture<Void>> allPipelines = new CopyOnWriteArrayList<>();

        getSportsAsync()
                .thenAccept(sports -> processSports(sports, allPipelines))
                .join();

        CompletableFuture
                .allOf(allPipelines.toArray(new CompletableFuture[0]))
                .join();

        logger.info("Processed events total ={}", processedEvents.sum());
        shutdownPools();
    }


    private void processSports(List<Sport> sports,
                               List<CompletableFuture<Void>> pipelines) {

        if (sports == null || sports.isEmpty()) {
            logger.error("No sports returned");
            return;
        }

        for (Sport sport : sports) {
            if (sport == null || sport.regions() == null) continue;
            for (Region region : sport.regions()) {
                if (region == null || region.leagues() == null) continue;
                for (League league : region.leagues()) {
                    if (league == null) continue;

                    pipelines.add(submitLeagueTask(league.id()));
                }
            }
        }
    }


    private CompletableFuture<Void> submitLeagueTask(long leagueId) {
        return CompletableFuture
                .supplyAsync(() -> getEventIds(leagueId), leaguePool)
                .thenComposeAsync(this::submitEventTasks, eventPool)
                .exceptionally(e -> {
                    logger.error("Pipeline failed for league ={}", leagueId, e);
                    return null;
                });
    }


    private CompletableFuture<Void> submitEventTasks(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<?>[] futures = eventIds.stream()
                .filter(id -> id != null && id > 0)
                .map(id -> CompletableFuture.runAsync(() -> {
                    Event data = getEventData(id);
                    if (data != null) {
                        logger.info(data.toString());
                        processedEvents.increment();
                    }
                }, eventPool))
                .toArray(CompletableFuture[]::new);


        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<List<Sport>> getSportsAsync() {
        return api.getSportsAsync(GET_SPORTS)
                .thenApply(mapper::toSport)
                .exceptionally(e -> {
                    logger.error("Failed to get sports", e);
                    return List.of();
                });
    }


    private List<Long> getEventIds(long leagueId) {
        return api.getLeagueDataAsync(leagueId)
                .thenApply(mapper::getEventIds)
                .exceptionally(e -> {
                    logger.error("Failed to extract event IDs for league ={}", leagueId, e);
                    return List.of();
                })
                .join();
    }

    private Event getEventData(long eventId) {
        return api.getEventDataAsync(eventId)
                .thenApply(mapper::toEventData)
                .exceptionally(e -> {
                    logger.error("Failed to print event data for event ={}", eventId, e);
                    return null;
                })
                .join();
    }

    private void shutdownPools() {
        leaguePool.shutdown();
        eventPool.shutdown();
    }
}
