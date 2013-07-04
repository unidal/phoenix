
projects = [];
projectsChosen = [];

function chosenProjects() {
					return projectsChosen;
				}
				
				function normalizeProject(project) {
					return project.replace(".", "-");
				}
		
				function loadProjectsTo(isAsync, doSth) {
					$.ajax({
						async: isAsync,
						url: "/req/listProjects",
						data: {projectNamePattern: '*'},
						dataType: 'json'
					}).done(doSth);
					return projects;
				}
		
				function matchPattern(regex) {
					return function(toCheck) {
						return regex.test(toCheck);
					}
				}
		
				function projectBtnClicked(btn) {
					var project = $(btn).attr("name");
					$(btn).toggleClass('active')
					if($(btn).hasClass('active')) {
						onProjectChosen(project);
					} else {
						onProjectUnChosen(project);
					}
				}
		
				$(function() {
				
					$("body").addClass("loading");
				
					// select first tab
					$('#projectTab a[href="#projectTab0"]').tab('show');
				
					loadProjectsTo(true, function(data) {
						var projects = data["projects"].sort();
						console.log("total number of projects: " + projects.length);
						
						var patterns = [];
						var gap = 3;
						for(var i = 0; i < 26; i += gap ) {
							var startChar = String.fromCharCode(97 + i);
							var endChar = String.fromCharCode(Math.min(97 + i + gap - 1, 97 + 25));
							patterns.push(new RegExp("^[" + startChar + "-" + endChar + "].*", ""));
						}
						
						for(var patternIdx = 0; patternIdx < patterns.length; patternIdx++) {
							var partialProjects = $.grep(projects, matchPattern(patterns[patternIdx]));
							var lis = [];
							var columns = 3;
							var totalTime = 0;
							$.each(partialProjects, function(i, item) {
								var firstTd = i%columns == 0;
								var lastTd = i%columns == columns - 1;
								if(firstTd) {
									lis.push("<tr>");
								}
								lis.push(sprintf('<td id="td-%2$s"><button type="button" name="%1$s" class="btn btn-phoenix" id="btn-%2$s" onclick="projectBtnClicked(this)">%1$s</button></td>', item, normalizeProject(item)));
								if(lastTd) {
									lis.push("</tr>");
								}
								if(i == partialProjects.length-1 && partialProjects.length%columns != 0) {
									var columnsMiss = columns - partialProjects.length%columns;
									for(var i = 0; i < columnsMiss; i++) {
										lis.push("<td></td>");
									}
									lis.push("</tr>");
								}
								var start = new Date().getTime();
								$("#projectTab" + patternIdx).html("<table>" + lis.join("") + "</table>");
								$("body").removeClass("loading");
								totalTime += (new Date().getTime()) - start;
							});
							console.log(totalTime);
						}
						
						// select existing projects
						$.ajax({
							async: false,
							url: "/req/workspaceMeta",
							dataType: 'json'
						}).done(function(data){
							var existingProjects = data["workspace"]["m_bizProjects"];
							if(existingProjects.length == 0) {
								$("#submit").html("Create");
							} else {
								$("#submit").html("Modify");
								for(var i = 0; i < existingProjects.length; i++) {
									var project = existingProjects[i]["m_name"];
									onProjectChosen(project);
								}
							}
						});
						
					});
					
				});
				
				function loadProjects() {
					loadProjectsTo(false, function(data) {
						projects = data["projects"];
					});
					return projects;
				}
				
				function canChooseProject(project) {
					return projects.indexOf(project) >= 0 && chosenProjects().indexOf(project) < 0;
				}
				
				function typeaheadMatcher(project) {
					if(!canChooseProject(project)) {
						return false;
					} else {
						return project.indexOf(this.query) >= 0;
					}
				}
				
				function typeaheadSorter(items) {
					var equalArr = [];
					var prefixArr = [];
					var containsArr = [];
					
					for(var i = 0; i < items.length; i++) {
						var item = items[i];
						if(item == this.query) {
							equalArr.push(item);
						} else if(item.indexOf(this.query) == 0) {
							prefixArr.push(item);
						} else {
							containsArr.push(item);
						}
					}
					
					equalArr.sort();
					prefixArr.sort();
					containsArr.sort();
					
					return equalArr.concat(prefixArr).concat(containsArr);
				}
				
				function removeElementFromArr(arr, element) {
					arr.splice(arr.indexOf(element),1);
				}
				
				function onProjectChosen(selectedProject) {
					var projectBtn = $("#btn-" + normalizeProject(selectedProject));
					projectBtn.addClass("active");
					projectsChosen.push(selectedProject);
					$("#projectsChosen").append(projectBtn);
				}
				
				function onProjectUnChosen(selectedProject) {
					var projectBtn = $("#btn-" + normalizeProject(selectedProject));
					projectBtn.removeClass("active");
					removeElementFromArr(projectsChosen, selectedProject);
					$("#td-" + normalizeProject(selectedProject)).append(projectBtn);
				}
				
				/*$(".tm-input").tagsManager({
						typeahead: true,
						blinkBGColor_1: '#FFFF9C',
						blinkBGColor_2: '#CDE69C',
						hiddenTagListId: 'projectsChosen',
						tagCloseIcon: 'x',
						typeaheadSource: loadProjects,
						typeaheadDelegate: {items: 8, matcher: typeaheadMatcher, sorter: typeaheadSorter},
						validator: canChooseProject
					});*/
					
				$("#typeahead").typeahead({
					source: loadProjects, 
					items: 8, matcher: 
					typeaheadMatcher, 
					sorter: typeaheadSorter,
					updater: onProjectChosen
				});
				
				// variable to hold request
				var request;
				// bind to the submit event of our form
				$("#foo").submit(function(event){
					if(projectsChosen.length == 0) {
						$("#submitError").html("Select at least one project");
						$("#submitError").addClass("alert alert-error");
						$("#submitError").show();
						event.preventDefault();
						return;
					}
					$("#submitError").hide();
				
				    // abort any pending request
				    if (request) {
				        request.abort();
				    }
				    // setup some local variables
				    var $form = $(this);
				    // let's select and cache all the fields
				    var $inputs = $form.find("input, select, button, textarea");
				    // serialize the data in the form
				    var serializedData = $form.serialize();
				    
				    serializedData = {projects: projectsChosen.join(",")};
				    
				    // alert(serializedData);
				
				    // let's disable the inputs for the duration of the ajax request
				    $inputs.prop("disabled", true);
				
				    // fire off the request to /form.php
				    request = $.ajax({
				        url: "/req/submitWorkspace",
				        type: "post",
				        data: serializedData
				    });
				
				    // callback handler that will be called on success
				    request.done(function (response, textStatus, jqXHR){
				        // log a message to the console
				    });
				
				    // callback handler that will be called on failure
				    request.fail(function (jqXHR, textStatus, errorThrown){
				        // log the error to the console
				        console.error(
				            "The following error occured: "+
				            textStatus, errorThrown
				        );
				    });
				
				    // callback handler that will be called regardless
				    // if the request failed or succeeded
				    request.always(function () {
				        // reenable the inputs
				        $inputs.prop("disabled", false);
				    });
				
				    // prevent default posting of form
				    event.preventDefault();
				});