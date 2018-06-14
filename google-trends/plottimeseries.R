library(lubridate) # for working with dates
library(ggplot2)  # for creating graphs
library(scales)   # to access breaks/formatting functions
library(gridExtra) # for arranging plots
library(ggthemes) # for themes


timeseries <- read.csv(file ="/home/abhishek/repositories/cis553/google-trends/timeseries.csv", header = TRUE, sep = ",")
timeseries$date <- as.Date(timeseries$date, "%d-%m-%Y")

mindate <- min(timeseries$date)
maxdate <- max(timeseries$date)

meanvalue <- mean(timeseries$value)
devval <- sd(timeseries$value)

timeplot <- ggplot(timeseries, aes(date, value)) +
            geom_point() +
            geom_line() +
            xlab("Date") + 
            ylab("Relative Popularity") +
            (scale_x_date(breaks=date_breaks("1 week"))) +
            theme(axis.text.x = element_text(angle = 90, hjust = 1)) +
            geom_hline(aes(yintercept = meanvalue), colour = 'red') +
            geom_hline(aes(yintercept = meanvalue + devval), colour = 'yellow')
            #+ theme(plot.title = element_text(lineheight=.8, face="bold", size = 20)) 
            #+ theme(text = element_text(size=18))
timeplot

#timeplot <- qplot(date, value, data=timeseries, xlab="Date", ylab="Relative Popularity")
#timeplot