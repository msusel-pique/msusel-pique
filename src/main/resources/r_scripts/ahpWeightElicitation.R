# In:
#   This script assumes a directory location is passed in as
#   a command argument. That directory must exist and must contain
#   a directory "Comparison_Matrices" that contains the .xls files
#   from ComparisonMatricesGenerator.generateCompMatrics().
#   These spreadsheets must also have the HAND-ENTERED upper diagonal
#   values filled in.
#
# Out:
#   Generates a 'weights.json' file in the same directory passed in
#   as args[1].


# Load the appropriate libraries
library(xlsx)
library(jsonlite)

# Move to the desired directory where the comparison matrices are placed
args <- commandArgs(trailingOnly = TRUE)
Dir <- args[1]
setwd(Dir)

# List the files found in this directory
files <- dir("./Comparison_Matrices")
setwd("./Comparison_Matrices")

first <- TRUE

# Iterate through each file found in the directory
for(file in files){
  print(file)
  # Read the xls and store its values in a dataframe
  # Read the data frame
  df <- read.xlsx(file, sheetIndex = 1, header = TRUE, stringsAsFactors=FALSE)

  # Keep only the values
  sub.df <- df[, -1]
  sapply(sub.df,as.numeric)
  
  # Complete the main diagonal with ones
  for(i in seq(1,ncol(sub.df))){
   # print(sub.df[[i,i]])
    sub.df[[i,i]] <- as.numeric(1)
  }
  
  # Complete the lower triangle with the reciprosal values of the upper
  for(i in c(2:nrow(df))){
    for(j in seq(1,i-1)){
      if(sub.df[[j,i]] != 0){
        sub.df[[i,j]] = 1 / as.numeric(sub.df[[j,i]])
      }else{
        print("Devision by zero avoided")
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

setwd(Dir)
write(json, "./weights.json")