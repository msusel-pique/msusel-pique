# In:
#   This script assumes a directory location is passed in as
#   a command argument. That directory must exist and must contain
#   the .csv files: one for TQI node and each QualityAspect node.
#   (the .csv files can be generated from ComparisonMatricesGenerator.generateCompMatrics()).
#   These spreadsheets must also have the HAND-ENTERED upper diagonal
#   values filled in.  Note: the hand-entered values can be in decimal
#   or fraction form, but if in fraction form they must be represented
#   as an improper fraction.
#
# Out:
#   Generates a 'weights.json' file in the directory passed in
#   as args[2].


# Load the appropriate libraries
library(jsonlite)

# Move to the desired directory where the comparison matrices are placed
args <- commandArgs(trailingOnly = TRUE)
input <- args[1]
output <- args[2]
setwd(input)

# List the files found in this directory
files <- dir(input)
first <- TRUE

# Iterate through each file found in the directory
for(file in files){
  # Read the csv and store its values in a dataframe
  df <- read.csv(file, fileEncoding="UTF-8-BOM", stringsAsFactors = F, header = T)

  # Keep only the values
  sub.df <- df[, -1]

  # Parse "x/y" fraction strings into numeric data
  sub.df <- apply(sub.df, 2, function(this.col) sapply(this.col, function(x) eval(parse(text = x))))
  
  # Complete the main diagonal with ones
  for(i in seq(1,ncol(sub.df))){
    sub.df[[i,i]] <- as.numeric(1)
  }
  
  # Complete the lower triangle with the reciprosal values of the upper
  for(i in c(2:nrow(sub.df))){
    for(j in seq(1,i-1)){
      if(sub.df[[j,i]] != 0){
        sub.df[[i,j]] = 1 / as.numeric(sub.df[[j,i]])
      }else{
        print("Division by zero avoided. This is okay if there are zeroed values in the AHP file.")
      }
    }
  }
  
  # Calculate eigenvalues/eigenvectors -> weights
  eig <- eigen(data.matrix(sub.df))
  vec <- eig$vectors[,1]
  s <- sum(vec)
  weights <- vec / s
  weights <- as.numeric(weights)
  
  # Add them to a list
  if(first){
    l <- list(weights)
    n <- names(df)
    char.names <- list(n[[1]])
    first <- FALSE
  }else{
    l <- c(l, list(weights))
    n <- names(df)
    char.names <- c( char.names , n[[1]])
  }
}

# Set the name of each characteristic
names(l) <- char.names

# Store the results to a json file
json <- toJSON(l)

setwd(output)
write(json, "./weights.json")