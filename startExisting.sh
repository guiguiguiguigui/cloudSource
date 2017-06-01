aws ec2 start-instances --instance-ids  "$(< existing_instances.txt)"
aws ec2 describe-instances --instance-ids "$(< existing_instances.txt)" --query 'Reservations[*].Instances[0].PublicIpAddress' > ips.txt
string="$(< ips.txt)"
set -f
IPS=( $(echo "$string" | grep -o '\".*\"') )
echo "${#IPS[@]}"
for ip in "${IPS[@]:1}"
do
	new=$(echo "$ip" | tr -d '\"')
	echo "Connecting to $new"
	ssh -i cloudsource1.pem ec2-user@"$new" "sh -c 'cd cloudSource/experiment; nohup ./startWorker > /dev/null 2>&1 &'" 
	sleep 5 
done

master=$(echo "${IPS[0]}" | tr -d '\"')
ssh -i cloudsource1.pem ec2-user@"$master" < runMaster.sh