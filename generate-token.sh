# Usage: ./generate-token.sh
#!/bin/bash
echo "API Token Management"
echo "------------------"
echo "1. Generate new token"
echo "2. List all tokens"
echo "3. Revoke a token"
echo "------------------"
read -p "Select option: " option

case $option in
  1)
    ./mvnw spring-boot:run -Dspring-boot.run.arguments=--generate-token
    ;;
  2)
    ./mvnw spring-boot:run -Dspring-boot.run.arguments=--list-tokens
    ;;
  3)
    ./mvnw spring-boot:run -Dspring-boot.run.arguments=--revoke-token
    ;;
  *)
    echo "Invalid option"
    ;;
esac