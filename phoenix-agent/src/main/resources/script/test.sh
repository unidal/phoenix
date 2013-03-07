#options=$(getopt -o ab:c: -l along,blong:,clong: -- "$@")
options=$(getopt -o x -l along,blong:,clong: -- "$@")
echo $options
eval set -- $options
while [ $# -gt 0 ]
do
case "$1" in
	-a | --along)	aflag="yes";;
			-b | --blong) bflag="$2";shift;;
				# for options with required arguments, an additional shift is required
					-c | --clong) cargument="$2";shift;;
						(--) shift;break;;
							(-*) echo "$0: error - unrecognized option $1"1>&2;exit 1;;
								(*) break;;
									esac
										shift
									done
											echo $bflag
											echo $cargument
