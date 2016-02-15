echo $1
echo $2
echo $3
content="$(curl --user "$1:$2" $3)"
echo "OPERATION PROCESSED."
echo "$content" 
