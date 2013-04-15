require 'mysql2'

def parse_url_rewrite(stringio)
  client=Mysql2::Client.new(:host => '192.168.8.41', :username => 'aspnet_user', :password => 'dp!@78()-=', :database => 'DianPing')
  results=client.query("select MaskURL, RealURL, PageId from DP_URLRewriteRulesNew")
  results.each do |row|
    mask_url = row["MaskURL"]
    real_url = row["RealURL"]
    page_id = row["PageId"]
    if mask_url
      mask_url = mask_url.slice(1..-1) if mask_url[0] == "~"
      real_url = real_url.slice(1..-1) if real_url[0] == "~"
      pattern = <<-EOF
       <url-rewrite-rule>
          <mask-url><![CDATA[#{mask_url}]]></mask-url>
          <real-url><![CDATA[#{real_url}]]></real-url>
          <page-id>#{page_id}</page-id>
      </url-rewrite-rule>
      EOF
      stringio << pattern
    end
  end
end