
if [ $# -lt 4 ];then
	echo "usage: maven.sh targetDir groupId artifactId version [type]"
	exit 1
fi

if [ ${1:0:1} == "/" ];then
	targetDir=$1
else
	targetDir=`pwd`/$1
fi
groupId=$2
artifactId=$3
version=$4
type=war
if [ $# -ge 5 ];then
	type=$5
fi


cwd=`pwd`
tmpdir=maventmp/$groupId/$artifactId/$version
rm -rf $tmpdir
mkdir -p $tmpdir
cd $tmpdir
cat <<-END > pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.dianping.platform</groupId>
	<artifactId>sample-app1</artifactId>
	<version>0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
	<dependencies>
		<dependency>
			<groupId>$groupId</groupId>
			<artifactId>$artifactId</artifactId>
			<version>$version</version>
			<type>$type</type>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-dependency-plugin</artifactId>
					<configuration>
						<outputDirectory>
							\${project.build.directory}
						</outputDirectory>
					</configuration>
			</plugin>
		</plugins>
	</build>
</project>
END
mvn -Dmaven.test.skip clean dependency:copy-dependencies
cp target/*.$type $targetDir >/dev/null 2>&1
cd $cwd
rm -rf $tmpdir
