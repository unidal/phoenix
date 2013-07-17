require 'open-uri'
require "net/https"  
require "uri"  
require 'nokogiri'

def get_http_response(url)
	uri = URI.parse(url)  
	http = Net::HTTP.new(uri.host, uri.port)  
	request = Net::HTTP::Get.new(uri.request_uri)  
	request.initialize_http_header({"Authorization" => "Basic cGVuZy5odTpxd2Vhc2Q="})  
	response = http.request(request)  
end

def get_html(url)
	response = get_http_response(url)
	response.body
end

def get_http_code(url)
	response = get_http_response(url)
	code = response.code
end

def parse_trunk_page(trunk_url)
	html = get_html trunk_url	
	doc = Nokogiri::HTML(html)
	doc.css('li').each do |li|
		a = li.child
		href = "#{a['href']}"
		child_url = trunk_url + href
		name = a.content
		name = name[-1]=="/"?name[0..-2]:name
		ignore_names = ["../", "pom.xml", "src/", "bin/", ".svnignore", ".gitignore", "setting.xml"]
		puts "#{name}=#{child_url}" unless ignore_names.include?(href) || get_http_code(child_url + "pom.xml") == "404"
	end
end

def parse_list_page(root_url)
	project_list_html = get_html root_url	
	project_list_doc = Nokogiri::HTML(project_list_html)
	project_list_doc.css('a').each do |project_anchor|
		href = project_anchor['href']
		unless href == "../"
			project_url = root_url + project_anchor['href']
			project_html = get_html project_url
			project_doc = Nokogiri::HTML(project_html)
			trunks = project_doc.css('a[href="trunk/"]')
			parse_trunk_page project_url + trunks[0]['href'] unless trunks.length==0 || trunks[0]['href']=='../'
		end
	end
end

top_dirs = "bi,demo,dev,dianping,dp-algorithm,dp-credit,dp-search,dptest,ezc,midas,platform,tuangou".split(",")
top_dirs.each do |dir|
	root_url = "http://192.168.8.45:81/svn/dianping/#{dir}/"
	parse_list_page root_url
end
