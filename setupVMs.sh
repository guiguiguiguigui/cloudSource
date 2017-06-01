echo first argument: $1 # image id with all files on disk & programs installed 
echo second argument: $2 # how many 
aws ec2 run-instances --image-id $1 --security-group-ids sg-1969bf62 --count $2 --instance-type t2.micro --key-name cloudsource1 --query 'Instances[*].InstanceId' > existing_instances.txt
aws ec2 describe-instances --instance-ids "$(< instances.txt)" --query 'Reservations[0].Instances[*].PublicIpAddress' >ips.txt
string="$(< ips.txt)"
set -f
IPS=( $(echo "$string" | grep -o '\".*\"') )
echo "${#IPS[@]}"
echo "${IPS[@]:1}" >> runMaster.sh