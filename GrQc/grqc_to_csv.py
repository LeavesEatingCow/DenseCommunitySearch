import os
import csv

# Function to convert scientific notation to normal notation
def convert_to_float(scientific_notation):
    return float("{:e}".format(float(scientific_notation)))

# Function to extract query and index creation times from the file
def extract_times(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()
        query_time = float(lines[0].split(":")[1].strip())
        index_time = convert_to_float(lines[1].split(":")[1].strip())
    return query_time, index_time

# Main function to process files and create CSV
def process_files(directory):
    # Initialize data structure to store information
    data = {'0.1': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.2': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.3': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.4': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.5': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.6': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.7': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.8': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '0.9': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []},
            '1.0': {'kcore': [], 'kcore_query': [], 'ktruss': [], 'ktruss_query': []}}

    # Iterate through files in the directory
    for file_name in os.listdir(directory):
        file_path = os.path.join(directory, file_name)

        # Check if the file is a kcore or ktruss file
        if file_name.endswith("cored.txt"):
            scaling_factor = file_name.split("_")[0]
            query_time, index_time = extract_times(file_path)

            # Update data structure with kcore information
            data[scaling_factor]['kcore'].append(index_time)
            data[scaling_factor]['kcore_query'].append(query_time)

        elif file_name.endswith("trussed.txt"):
            scaling_factor = file_name.split("_")[0]
            query_time, index_time = extract_times(file_path)

            # Update data structure with ktruss information
            data[scaling_factor]['ktruss'].append(index_time)
            data[scaling_factor]['ktruss_query'].append(query_time)

    # Write data to CSV
    with open('output.csv', 'w', newline='') as csvfile:
        fieldnames = ['Scaling Factor', 'kcore', 'kcore_query', 'ktruss', 'ktruss_query']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        # Write CSV header
        writer.writeheader()

        # Write data to CSV
        for scaling_factor, values in data.items():
            row = {'Scaling Factor': scaling_factor,
                   'kcore': sum(values['kcore']) / len(values['kcore']),
                   'kcore_query': sum(values['kcore_query']) / len(values['kcore_query']),
                   'ktruss': sum(values['ktruss']) / len(values['ktruss']),
                   'ktruss_query': sum(values['ktruss_query']) / len(values['ktruss_query'])}

            writer.writerow(row)

# Specify the directory where your project files are located
project_directory = '/Users/danielmaliro/Downloads/DenseCommunitySearch/GrQc'

# Call the main function to process files and create CSV
process_files(project_directory)
