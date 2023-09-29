## The Match Game

You receive movie data feeds from many different providers, ex: Google Play, VUDU, Amazon Instant.
These include information about the availability of a given movie or TV show. 
They also usually include some metadata from  the movie, such as the title, director, and release year, alongside availability information like price and the fulfilment URL.

How do you take metadata and availability information found in these provider feeds and match them to an internal database of movies? 
Different feeds have varying types of metadata and there are inconsistencies between sources of data.

In this exercise, you will be given a database of movies and a provider feed. 
You must implement your own matching algorithm to match availability information to the internal database of movies.

## Getting Started

You will find some files given to you to start off. Feel free to bring in any additional libraries that you feel will help
you accomplish this task.

## Data Files

**movies.csv** : a comma-separated file of around 200,000 movies, with the following schema:

| id | title | year |
| ------------ | ----- | ---- |
| 1            | Finding Nemo | 2006 |

**actors_and_directors.csv**: a comma-separated file of actors and directors associated with each movie. It has the following schema:

| movie_id | name | role |
| ------------ | ---- | -------- |
| 1            | Leonardo DiCaprio | cast |
| 2            | Martin Scorsese | director|

**xbox.csv**: a comma-separated file of the official provider feed for Xbox. It has the following schema:

| MediaId | Title | OriginalReleaseDate | MediaType | Actors | Director | XboxLiveURL |
| ------- | ----- | ------------------- | --------- | ------ | -------- | ----------- |
| 531b964f-0cb9-4968-9b77-e547f2435225| Furious 7 | 4/13/2015 | Movie | Vin Diesel, Paul Walker, Jason Statham | James Wan | video.xbox.com