DROP ALL OBJECTS;

CREATE TABLE faculties(
faculty_id SERIAL PRIMARY KEY,
faculty_short_name text,
faculty_full_name text);

CREATE TABLE groups(
group_id SERIAL PRIMARY KEY,
group_name text,
faculty_id integer REFERENCES faculties(faculty_id) ON DELETE RESTRICT);

CREATE TABLE students(
student_id SERIAL PRIMARY KEY,
first_name text,
last_name text,
group_id integer REFERENCES groups(group_id) ON DELETE RESTRICT);

CREATE TABLE teachers(
teacher_id SERIAL PRIMARY KEY,
first_name text,
last_name text,
faculty_id integer REFERENCES faculties(faculty_id) ON DELETE RESTRICT);

CREATE TABLE courses(
course_id SERIAL PRIMARY KEY,
course_name text,
course_description text,
teacher_id integer REFERENCES teachers(teacher_id) ON DELETE RESTRICT);

CREATE TABLE classrooms(
classroom_id SERIAL PRIMARY KEY,
classroom_number text,
capacity integer);

CREATE TABLE timeslots(
timeslot_id SERIAL PRIMARY KEY,
timeslot_description text);

CREATE TABLE lessons(
lesson_id SERIAL PRIMARY KEY,
lesson_date date NOT NULL,
timeslot_id integer NOT NULL REFERENCES timeslots(timeslot_id) ON DELETE RESTRICT,
course_id integer NOT NULL REFERENCES courses(course_id) ON DELETE RESTRICT,
classroom_id integer NOT NULL REFERENCES classrooms(classroom_id) ON DELETE RESTRICT,
UNIQUE (lesson_date, timeslot_id, course_id),
UNIQUE (lesson_date, timeslot_id, classroom_id));

CREATE TABLE groups_courses(
group_id integer REFERENCES groups(group_id) ON DELETE RESTRICT,
course_id integer REFERENCES courses(course_id) ON DELETE RESTRICT,
PRIMARY KEY (group_id, course_id));


INSERT INTO timeslots (timeslot_description)
VALUES 
('09:00 - 10:30'),
('10:30 - 12:00'),
('12:30 - 14:00'),
('14:00 - 15:30'),
('15:30 - 17:00'),
('17:00 - 18:30');

INSERT INTO classrooms (classroom_number, capacity)
VALUES 
('101A', 30),
('102A', 300);


INSERT INTO faculties (faculty_short_name, faculty_full_name)
VALUES
('CS', 'Computer Science'),
('BA', 'Ballet Art');


INSERT INTO groups (group_name, faculty_id) 
VALUES 
('cs-20', 1);


INSERT INTO students (first_name, last_name, group_id)
VALUES
('John', 'Smith',1 ),
('R2D2' , null,1);

INSERT INTO teachers (first_name, last_name, faculty_id)
VALUES
('Alan', 'Turing', 1);

INSERT INTO courses (course_name, course_description, teacher_id)
VALUES
('Turing machine', 'Turing machine theory', 1),
('Turing-complete languages', 'Basics of programming langs', 1);	

INSERT INTO groups_courses (group_id, course_id)
VALUES
(1, 1),
(1, 2);

INSERT INTO lessons (lesson_date, timeslot_id, course_id, classroom_id)
VALUES
('2020-06-18', 1, 1, 1),
('2020-06-18', 2, 1, 1);
