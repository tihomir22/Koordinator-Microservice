package com.koordinator.epsilon.Koordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class KoordinatorApplication {

	public static void main(String[] args) {
		/*TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName("my_2017_series").build();
		ZonedDateTime endTime = ZonedDateTime.now();
		series.addBar(endTime, 105.42, 112.99, 104.01, 111.42, 1337);
		series.addBar(endTime.plusDays(1), 111.43, 112.83, 107.77, 107.99, 1234);
		series.addBar(endTime.plusDays(2), 107.90, 117.50, 107.90, 115.42, 4242);
// Getting the simple moving average (SMA) of the close price over the last 5 ticks
		ClosePriceIndicator closed=new ClosePriceIndicator(series);
		SMAIndicator shortSma = new SMAIndicator(closed, 5);

		SMAIndicator longSma = new SMAIndicator(closed, 3);
		for(int i=0;i<longSma.getTimeSeries().getBarCount();i++){
			System.out.println(longSma.getValue(i));
		}*/
		SpringApplication.run(KoordinatorApplication.class, args);
	}


	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}
}
