# Schedule-Generator
## Overarching goal of the project:
### Prompt user for desired courses
### Retrieve course times and information from school webpage
### Prompt user for time preferences
### Generate schedules around said preferences into image files to aid in decision-making process before class registration
## Current Functionality
### 1) Class Information Gathering
#### Prompts user to input term, subject, and course number information for several courses
#### Queries school webpage for input course information
#### Obtains html dumps of generated webpages
#### Parses html dump and generates text file for Schedule Generator input
### 2) Schedule Generation with preferences
#### Reads formatted input file
#### Generates grid of check boxes and prompts user to select time preferences on grid
#### Offers operation repetition for easier preference selection
#### Generates new input file based on preferences
#### Generates schedule images based on edited input file
#### Outputs image files and corresponding color key
#### Displays final schedule count and location of files
# © 2016-2017 Caleb Tung and Alexander Cockrum, all rights reserved. 
