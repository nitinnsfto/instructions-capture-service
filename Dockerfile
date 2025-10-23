# Use a base image from Docker Hub
FROM alpine:3.14

# Set the working directory inside the container
WORKDIR /app

# Copy the application files from the host machine to the container
COPY . .

# Expose a port for the application (optional)
EXPOSE 8080

# Define the command to run when the container starts
CMD ["./myapp"]