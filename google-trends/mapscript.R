library(ggplot2)
library(ggmap)
library(maps)
library(mapdata)

usa <- map_data("usa")

# Add a new dataframe with new locations
labs <- data.frame(
  long = c(-122.064873, -122.306417, -123.086754),
  lat = c(36.951968, 47.644855, 44.052071),
  names = c("SWFSC-FED", "NWFSC", "Eugene"),
  stringsAsFactors = FALSE
)

ggplot() + 
# By default fills Black, and no color
geom_polygon(data = usa, aes(x=long, y = lat, group = group), 
             fill = NA, color = "red") + 
coord_fixed(1.3) +
# Highlight all locations
geom_point(data = labs, aes(x = long, y = lat), color = "black", size = 6) +
geom_point(data = labs, aes(x = long, y = lat), color = "yellow", size = 4)
