library(ggplot2)
library(ggmap)
library(maps)
library(mapdata)
library(dplyr)

usa <- map_data("usa")

# Add a new dataframe with new locations
labs <- data.frame(
  long = c(-122.064873, -122.306417, -123.086754),
  lat = c(36.951968, 47.644855, 44.052071),
  names = c("SWFSC-FED", "NWFSC", "Eugene"),
  stringsAsFactors = FALSE
)

#ggplot() + 
# By default fills Black, and no color
#geom_polygon(data = usa, aes(x=long, y = lat, group = group), 
#             fill = NA, color = "red") + 
#coord_fixed(1.3) +
# Highlight all locations
#geom_point(data = labs, aes(x = long, y = lat), color = "black", size = 6) +
#geom_point(data = labs, aes(x = long, y = lat), color = "yellow", size = 4)

states <- map_data("state")
processed <- read.csv(file ="/home/abhishek/repositories/cis553/google-trends/regional.csv", header = TRUE, sep = ",")
analyzed <- inner_join(states, processed, by="region")

ditch_the_axes <- theme(
  axis.text = element_blank(),
  axis.line = element_blank(),
  axis.ticks = element_blank(),
  panel.border = element_blank(),
  panel.grid = element_blank(),
  axis.title = element_blank()
)

dim(analyzed)
regionalPlot <- ggplot(data = analyzed) + 
                geom_polygon(aes(x = long, y = lat, fill = value, group = group), color = "white") + 
                coord_fixed(1.3) +
                theme_bw() +
                ditch_the_axes
regionalPlot
png(filename="/home/abhishek/repositories/cis553/google-trends/regional.png")
plot(regionalPlot)
dev.off()