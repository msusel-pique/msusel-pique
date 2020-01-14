# In:
#   This script assumes a directory location is passed in as
#   command argument args[1]. That directory must exist and must contain
#   a file "benchmark_data.csv" that contains the .csv file representing
#   property findings per project
#
# Out:
#   Generates a 'threshold.json' file in the a directory passed in
#   as args[2]. threshold.json represents the calculation of the thresholds of the
#   Quality Model's properties.


# Set the working directory to the appropriate path
args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
output <- args[2]

setwd(input)

# Load the appropriate libraries
library(jsonlite)

# Read the data frame
df <- read.csv("benchmark_data.csv", fileEncoding="UTF-8-BOM", stringsAsFactors = F, header = T)

# drop the left column (assumes the left column is the project names)
df <- df[,-1]

#Create an empty vector
first <- TRUE

#Create an empty vector
first <- TRUE

# Iterate through the properties
for(i in 1:ncol(df)){
    #Get the current column (i.e property)
    # TODO: ensure script is robust against unexpected data formats (e.g. '2 1/2' instead of 5/2)
    property <- df[[i]]

    #Calculate the middle threshold
    t2 <- median(property)

    #Calculate the lower threshold (the minimum non-outlier observation)
    threshold <- quantile(property, c(0.25)) - 1.5 * IQR(property)
    index <- (property >= threshold)
    part <- property[index]
    t1 <- min(part)

    #Calculate the upper threshold (the maximum non-outlier observation)
    threshold <- quantile(property, c(0.75)) + 1.5 * IQR(property)
    index <- (property <= threshold)
    part <- property[index]
    t3 <- max(part)

    #Add a new row to the exported data frame
    if(first == TRUE){
        t <- c(t1, t2, t3)
        first <- FALSE
    }else{
        t <- rbind(t, c(t1,t2,t3))
    }
}

# Create the dataframe with the thresholds
rownames <- names(df)
colnames <- list("t1","t2","t3")
thresholds <- data.frame(t, row.names = rownames)
names(thresholds) <- colnames

# Export the results in json format
json <- toJSON(thresholds, digits = 8)
setwd(output)
write(json, "threshold.json")